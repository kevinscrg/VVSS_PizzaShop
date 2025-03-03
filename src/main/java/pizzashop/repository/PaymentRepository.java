package pizzashop.repository;

import pizzashop.model.Payment;
import pizzashop.model.PaymentType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PaymentRepository {
    private static String filename = "data/payments.txt";
    private List<Payment> paymentList;

    public PaymentRepository(){
        this.paymentList = new ArrayList<>();
        readPayments();
    }

    private void readPayments() {
        File file = new File(filename);

        if (!file.exists()) {
            System.err.println("Error: The payment file '" + filename + "' was not found. Creating a new empty file.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    Payment payment = getPayment(line);
                    if (payment != null) {
                        paymentList.add(payment);
                    } else {
                        System.err.println("Warning: Skipping invalid payment entry: " + line);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error: Invalid number format in payment entry: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Could not read from file '" + filename + "'. Please check file permissions.");
        }
    }


    private Payment getPayment(String line){
        Payment item=null;
        if (line==null|| line.equals("")) return null;
        StringTokenizer st=new StringTokenizer(line, ",");
        int tableNumber= Integer.parseInt(st.nextToken());
        String type= st.nextToken();
        double amount = Double.parseDouble(st.nextToken());
        item = new Payment(tableNumber, PaymentType.valueOf(type), amount);
        return item;
    }

    public void add(Payment payment){
        paymentList.add(payment);
        writeAll();
    }

    public List<Payment> getAll(){
        return paymentList;
    }

    public void writeAll(){
        //ClassLoader classLoader = PaymentRepository.class.getClassLoader();
        File file = new File(filename);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Payment p : paymentList) {
                System.out.println(p.toString());
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}