package bftsmart.demo.counter.helperFunctions;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Properties;

public class ProcessLayerConfig {
    public static Properties config;

    public ProcessLayerConfig(String config_file_path) {
        Properties config = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(config_file_path);
            config.load(inputStream);
            inputStream.close();
            System.out.println("PROPERTY " + config.getProperty("replica_name"));
            String functionName = config.getProperty("function");
            String[] parts = functionName.split("\\.");
            String className = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
            String methodName = parts[parts.length - 1];
            Class<?> cls = Class.forName(className);

            String argsString = config.getProperty("function.args");
            String[] func_args = argsString.split(",");
            Class<?>[] argTypes = new Class<?>[func_args.length];
            Object[] argValues = new Object[func_args.length];

            for (int i = 0; i < func_args.length; i++) {
                String[] arg_parts = func_args[i].split(":");
                argTypes[i] = getClass(arg_parts[0]);
                try {
                    argValues[i] = arg_parts[1];
                } catch (Exception e) {
                    argValues[i] = null;
                }


            }

            this.config = config;
            Method method = cls.getDeclaredMethod(methodName, argTypes);
            Object obj = cls.newInstance();
            method.invoke(obj,2.0);
//            System.out.println("HALFED RESULT " + result);

        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("Failed to load config");
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties getConfig() {
        return config;
    }

    private static Class<?> getClass(String className) throws ClassNotFoundException {
        switch (className) {
            case "int":
                return int.class;
            case "double":
                return double.class;
            default:
                return Class.forName(className);
        }
    }
}
