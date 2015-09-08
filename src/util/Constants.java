package util;

public class Constants {

    public static final String fullSourceNameDelimiter = "__NICE_LITTLE_DELIMITER__";
    public static final int localServerPortNumber = 8888;
    public static final int connectTimeout = 5000;  //  Timeout if connection has not been established after 5 seconds.
    public static final int readTimeout = 5000; //  Timeout if input has not been available after 5 seconds.
    public static final int sourceExecutionTimeout = 5000; //    Timeout if a source executed for more than 5 seconds without returning.

}
