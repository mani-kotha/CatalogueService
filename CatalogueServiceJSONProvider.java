package com.gmart.productcatalogue.resource.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.springframework.util.StreamUtils;

import com.gmart.productcatalogue.model.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
@Named
/** Base class for REST providers using the GSON library to produce/consume JSON. */
public class CatalogueServiceJSONProvider implements MessageBodyReader<Object>,
		MessageBodyWriter<Object> {

	/** Default encoding for JSON, if not specified by the media type. */
	private static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

	/** GSON worker. */
	private Gson gson;

	/** Classes supported for serialization/deserialization by this provider. */
	private final Set<Class<?>> supportedClasses;

	public CatalogueServiceJSONProvider() {
		gson = new GsonBuilder().create();
		supportedClasses = new HashSet<>();
		supportedClasses.add(Product.class);
		supportedClasses.add(ArrayList.class);
	}

	public long getSize(Object arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4) {
		return 0L;
	}

	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return supportedClasses.contains(type);
	}

	public void writeTo(Object obj, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> mvMap, OutputStream outputStream)
			throws IOException, WebApplicationException {
		StringWriter sw = new StringWriter();
		gson.toJson(obj, type, sw);
		String json = sw.toString();
		StreamUtils.copy(json, DEFAULT_ENCODING, outputStream);
	}

	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return supportedClasses.contains(type);
	}

	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> mvMap, InputStream inputStream)
			throws IOException, WebApplicationException {
		String json = StreamUtils.copyToString(inputStream, DEFAULT_ENCODING);
		return gson.fromJson(json, type);
	}
}
