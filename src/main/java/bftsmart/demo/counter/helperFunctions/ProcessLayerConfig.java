package bftsmart.demo.counter.helperFunctions;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Properties;

public class ProcessLayerConfig {
    public Properties config = new Properties();
    private Object obj;
    private int num_function_params;

    public ProcessLayerConfig(String config_file_path) {
//        Properties config = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(config_file_path);
            config.load(inputStream);
            inputStream.close();
            System.out.println("PROPERTY " + config.getProperty("replica_name"));

            this.config = config;

        } catch (IOException e) {
            System.out.println("Failed to load config");
        }
    }

    public Properties getConfig() {
        return this.config;
    }

    public int get_num_func_args() { return this.num_function_params; }

    public Method get_function_interface() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        // Get the function path including filename
        String functionName = this.config.getProperty("function");
        // Split string based on "."
        String[] parts = functionName.split("\\.");
        // The class name path is are all the elements except the last one
        String className = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
        // The mthod name is the last element
        String methodName = parts[parts.length - 1];
        // Initialize the class
        Class<?> cls = Class.forName(className);
        // Get the function arguments from the config file
        String argsString = this.config.getProperty("function.args");
        // Split function arguments into a String array
        String[] func_args = argsString.split(",");
        // Define the types of each argument
        String[] consume_stream_ids = this.config.getProperty("consume_from.stream_ids").split("\\,");
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
        this.num_function_params = consume_stream_ids.length;
        // Declare method with the arguments and types
        Method method = cls.getDeclaredMethod(methodName, argTypes);
        // Create an instance of the method
        this.obj = cls.newInstance();
        // example invocation
//        method.invoke(obj,2.0);
        return method;
    }

    public Object get_function_obj() {return this.obj;}

    private static Class<?> getClass(String className) throws ClassNotFoundException {
        switch (className) {
            case "int":
                return int.class;
            case "double":
                return double.class;
            case "double[]":
                return double[].class;
            case "Double[]":
                return Double[].class;
            default:
                return Class.forName(className);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ProcessLayerConfig p = new ProcessLayerConfig("/Users/roy/Github-Repos/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer1.properties");
        System.out.println("hi");
        Method m = p.get_function_interface();
        Object obj = p.get_function_obj();
        int num_args = p.get_num_func_args();
        double[] l = new double[1];
        l[0] = 5;
        System.out.println(m.invoke(obj, l));



    }
}
