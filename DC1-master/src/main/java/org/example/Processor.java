    package org.example;
    import java.io.File;
    import java.lang.Math;
    import java.io.IOException;
    import java.io.PrintWriter;
    import java.net.Socket;


    /**
     * Processor of HTTP request.
     */
    public class Processor<T> extends Thread {
        private final Socket socket;
        private final HttpRequest request;
        private final ThreadSafeQueue<T> queue;

        public Processor(Socket socket, HttpRequest request, ThreadSafeQueue<T> queue) {
            this.socket = socket;
            this.request = request;
            this.queue=queue;
        }
        @Override
        public void run() {
            try {
                while (true) {
                    // Wait for new element.
                    T elem = queue.pop();

                    // Stop consuming if null is received.
                    if (elem == null) {
                        return;
                    }

                    // Process element.
                    System.out.println(request + ": get item: " + elem);
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        





        public void process() throws IOException {
            // Print request that we received.
            System.out.println("Got request:");
            System.out.println(request.toString());
            System.out.flush();

            // To send response back to the client.
            PrintWriter output = new PrintWriter(socket.getOutputStream());

            // We are returning a simple web page now.
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Hello</title></head>");
            output.println("<body><p>Hello, world!</p></body>");
            output.println("</html>");
            output.flush();
            request(output);



            socket.close();
        }
        public void request(PrintWriter output) throws IOException {
            StringBuilder string = new StringBuilder(request.getRequestLine());
            System.out.println(string);
            string.delete(0,4);
            string.delete(string.length() - 9, string.length() + 1);
            System.out.println();
            System.out.println(string);
            System.out.println();

            if(string.toString().contains("/create/")) {
                string.delete(0,8);
                File value = new File(string.toString());
                try {
                    boolean new_value = value.createNewFile();
                    if (new_value) {
                        System.out.println("New Java File is created.");
                        output.println("<html>");
                        output.println("<body><p>" + string + " is created.</p></body>");
                    }
                    else {
                        System.out.println("The file already exists.");
                        output.println("<html>");
                        output.println("<body><p>" + string + " already exists.</p></body>");
                    }
                    output.println("</html>");
                    output.flush();
                }
                catch(Exception e) {
                    e.getStackTrace();
                }
            }else if(string.toString().contains("/delete/")) {
                string.delete(0,8);
                File value = new File(string.toString());
                try {
                    boolean old_value = value.delete();
                    if (old_value) {
                        System.out.println("Java File is deleted.");
                        output.println("<html>");
                        output.println("<body><p>" + string + " is deleted.</p></body>");
                    }
                    else {
                        System.out.println("The file does not exists.");
                        output.println("<html>");
                        output.println("<body><p>" + string + " does not exists.</p></body>");
                    }
                    output.println("</html>");
                    output.flush();
                }
                catch(Exception e) {
                    e.getStackTrace();
                }
            }
             else if (string.toString().contains("/exec/")) {
                string.delete(0,6);
                int number = Integer.parseInt(string.toString());
                System.out.println("Writing a "+number+" random big numbers.");
                output.println("<html>");
                output.println("<body>");
                for(int i=0;i<number;i++){
                    output.println("<p>"+Math.round(Math.random()*1000)+"</p>");
                }
                output.println("</body>");
                output.println("</html>");
                output.flush();
            }
        }
    }


