package db.models;

import java.lang.reflect.InvocationTargetException;

public abstract class BaseModel {
	public static <T extends BaseModel> T getInstance(Class<T> tClass, String[] data) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return tClass.getDeclaredConstructor(String[].class).newInstance(new Object[]{data});
	};
}
