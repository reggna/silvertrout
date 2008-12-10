package jbt;

import java.io.File;
import java.io.FileInputStream;

class PluginClassLoader extends ClassLoader {

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if(name.startsWith("jbt.plugins")) {
      name = name.replace('.', '/');
      try {
        File f = new File( name + ".class");
        if(f.exists()) {
          byte[] bytes  = new byte[(int)f.length()]; //Fix possible error when long > Integer.MAX
          FileInputStream s = new FileInputStream(f);
          for(int i = 0; i < f.length(); i++) {
            bytes[i] = (byte)s.read();
          }
          return defineClass(null, bytes, 0, (int)f.length());
        } else { System.out.println("File don't exists: " + name + ".class"); }
      } catch(Exception e) { }
    } else { System.out.println("Wrong prefix"); }
    throw new ClassNotFoundException(name + " is not a plugin");
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    if(name.startsWith("jbt.plugins")) {
      return findClass(name);
    } else {
      return super.loadClass(name, resolve);
    }
  }

}

