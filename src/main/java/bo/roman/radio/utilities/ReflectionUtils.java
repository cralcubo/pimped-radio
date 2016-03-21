package bo.roman.radio.utilities;

import java.lang.reflect.Field;

public interface ReflectionUtils {
	
	/**
	 * Gets the value of a private static final property.
	 * 
	 * @param obj is the Object with the constant with the value to get.
	 * @param constantName is the name of the property which value is needed. 
	 * @return the value of the property.
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	static Object getPrivateConstant(Object obj, String constantName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field searchPageTemplate = obj.getClass().getDeclaredField(constantName);
		searchPageTemplate.setAccessible(true);
		return searchPageTemplate.get(obj);
	}

}
