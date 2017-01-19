package com.github.apm.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;

/**
 * Agent切入主类
 * 
 * @author liuyang
 *
 */
public class AgentLauncher {

  public static void premain(String args, Instrumentation inst) {
    main(args, inst);
  }

  public static void agentmain(String args, Instrumentation inst) {
    main(args, inst);
  }


  private static synchronized void main(final String args, final Instrumentation inst) {
    ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
    try {
      // 传递的args参数分两个部分:agentJar路径和agentArgs
      // 分别是Agent的JAR包路径和期望传递到服务端的参数 /home/dc/log
      String path = System.getProperty("user.dir");
      System.out.println(path + "  args: " + args);
      if (path.endsWith("/bin")) {
        path = path.replace("/bin", "");
      }
      // inst.appendToBootstrapClassLoaderSearch(new JarFile(
      // AgentLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile()));
      String configLocation = "";
      String startClass = "";
      boolean sysjar = false;
      System.out.println("######2233" + args);
      if (args != null) {
        System.out.println("######22" + args);
        String[] array = args.trim().split(";");
        for (String param : array) {
          if (param.startsWith("-path:")) {
            path = param.substring(6);
            System.out.println("######" + path);
          }
        }
        for (String param : array) {
          if (param.startsWith("-conf:")) {// 配置文件
            param = param.substring(7);
            String absouthPath = param.startsWith("/") ? "" : path + File.separator;
            configLocation = absouthPath + param;
          } else if (param.startsWith("-sysjar:")) {// 第三方jar
            sysjar = true;
            param = param.substring(8);
            String[] sysJars = param.split(":");
            String absouthPath = param.startsWith("/") ? "" : path + File.separator;
            for (String s : sysJars) {
              LoadJarUtil.loadJar(absouthPath + s, inst);
            }
          } else if (param.startsWith("-starter:")) {// 启动类
            param = param.substring(9);
            if (!param.contains(".")) {// 全路径
              param = "com.github.apm.core." + param;
            }
            startClass = param;
          }
        }
      }

      if (startClass.length() == 0) {
        startClass = "com.github.apm.core.ApmMonitor";
      }
      if (configLocation.length() == 0) {
        configLocation = path + File.separator + "conf/apm.properties";
      }
      if (!sysjar) {
        System.out.println("jar file path:" + path + File.separator + "lib");
        LoadJarUtil.loadJar(path + File.separator + "lib" + File.separator, inst);
      }

      currentLoader.loadClass(startClass)
          .getMethod("outerInit", String.class, Instrumentation.class)
          .invoke(null, configLocation, inst);// 启动监听器
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
