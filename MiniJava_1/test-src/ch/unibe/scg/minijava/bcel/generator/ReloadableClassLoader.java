package ch.unibe.scg.minijava.bcel.generator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ReloadableClassLoader extends ClassLoader {
	public ReloadableClassLoader(ClassLoader parent) {
		super(parent);
	}

	public Class reloadClass(String name) throws ClassNotFoundException {
		try {
			File file = new File("bin/" + name.replaceAll("\\.", "/") + ".class");
			URL myUrl = file.toURI().toURL();
			URLConnection connection = myUrl.openConnection();
			InputStream input = connection.getInputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = input.read();

			while (data != -1) {
				buffer.write(data);
				data = input.read();
			}

			input.close();

			byte[] classData = buffer.toByteArray();

			return defineClass(name, classData, 0, classData.length);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
