package com.dnlgdev.feignwebclient;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/*
 * @author dglod
 */
@Component
public class InvocationHandler implements java.lang.reflect.InvocationHandler
{

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Lazy
	private List<WebClient> webClients;

	@Autowired
	@Lazy
	private Map<String, WebClient> webClientss;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
	{
		switch (method.getName())
		{
			case "hashCode":
				return System.identityHashCode(proxy);
			case "toString":
				return proxy.getClass() + "@" + System.identityHashCode(proxy);
			case "equals":
				return proxy == args[0];
		}

		return doInvoke(proxy, method, args);
	}

	@SneakyThrows
	private Object doInvoke(Object proxy, Method method, Object[] args)
	{

		var customClientAnnotation = method.getDeclaringClass().getAnnotation(CustomClient.class);
		if (customClientAnnotation == null)
			throw new IllegalArgumentException("Annotation 'CustomClient' not found");

		var beanName = customClientAnnotation.name();
		var webClientBeanName = Util.getWebClientName(beanName);

		var webClient = applicationContext.getBean(webClientBeanName, WebClient.class);

		var requestDTO = initRequestDTO(method, args);

		Mono<?> mono;
		if (requestDTO.isGet())
		{
			mono = webClient.get() //
					.uri(requestDTO.getUrl()) //
					.retrieve() //
					.bodyToMono(method.getReturnType());
		}
		else if (requestDTO.isPost())
		{
			mono = webClient.post() //
					.uri(requestDTO.getUrl()) //
					.bodyValue(requestDTO.getBody()) //
					.retrieve() //
					.bodyToMono(method.getReturnType());
		}
		else
		{
			throw new IllegalArgumentException(String.format("RequestMethod '%s' not supported", requestDTO.getMethod()));
		}

		mono.log();
		return mono.block();
	}

	private RequestDTO initRequestDTO(Method method, Object[] args)
	{
		var dto = new RequestDTO();

		RequestMapping annotation = method.getAnnotation(RequestMapping.class);
		if (annotation == null)
			throw new IllegalArgumentException(method.getDeclaringClass().getSimpleName() + ": Annotation 'RequestMapping' must be exists for method");

		// requestMethod
		{
			var requestMethod = annotation.method();
			if (requestMethod.length != 1)
				throw new IllegalArgumentException("RequestMapping: There must be one requestMethod");

			dto.setMethod(requestMethod[0]);
		}

		// url
		{
			var urlParts = annotation.value();
			if (urlParts.length == 0)
				throw new IllegalArgumentException("RequestMapping: There must be at least one value");

			dto.setUrl(String.join("", Arrays.asList(urlParts)));
		}

		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		int i = 0;
		for (Annotation[] a : parameterAnnotations)
		{
			for (Annotation aa : a)
			{

				if (aa instanceof RequestBody)
				{
					var r = (RequestBody) aa;
					if (dto.getBody() != null)
						throw new IllegalArgumentException("Only one body may exists");
					dto.setBody(args[i]);
				}
				if (aa instanceof RequestParam)
				{
					var r = (RequestParam) aa;
					dto.getRequestParams().add(String.format("%s=%s", //
							r.value(), //
							args[i]));
				}
				else if (aa instanceof PathVariable)
				{
					var r = (PathVariable) aa;
					dto.getPathVariables().put(r.value(), args[i]);
				}
			}
			i++;
		}

		return dto;
	}

	@Data
	static class RequestDTO
	{

		RequestMethod       method;
		String              url;
		List<String>        requestParams = new ArrayList<>();
		Map<String, Object> pathVariables = new HashMap<>();
		Object              body;

		boolean isGet()
		{
			return RequestMethod.GET.equals(method);
		}

		boolean isPost()
		{
			return RequestMethod.POST.equals(method);
		}

		String getUrl()
		{

			// pathVariables
			pathVariables.forEach((key, value) -> {
				var placeholder = String.format("{%s}", key);
				if (!url.contains(placeholder))
					throw new IllegalArgumentException("no param placeholder for: " + placeholder);

				url = url.replace(placeholder, value.toString());
			});

			// requestParams
			if (!requestParams.isEmpty())
			{
				url += "?";
				url += String.join("&", requestParams);
			}

			return url;
		}
	}

}