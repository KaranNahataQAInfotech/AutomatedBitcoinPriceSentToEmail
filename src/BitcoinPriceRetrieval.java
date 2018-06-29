import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class Values {
	String currencyType;
	String zebPay_Price; // ,coinGecko_Price;

	Values() {
		zebPay_Price = null;
	}
}

public class BitcoinPriceRetrieval {
	static ArrayList<Values> data = new ArrayList<>();
	static WebDriver driver1, driver2;

	public static void main(String args[]) throws InterruptedException {
		driver1 = new ChromeDriver();
		openZebPay();
		fetchAndStorePrices();
		driver1.quit();
		sendEmail();
	}

	private static void sendEmail() {
		String from = "@qainfotech.com";
		String toUserOne = "@gmail.com";
		String toUserTwo = "@gmail.com";
		String host = "smtp.qainfotech.com";
		String password = "XXXXXXX";
		// Get the session object
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			InternetAddress[] recepients=new InternetAddress[2];
			recepients[0]=new InternetAddress(toUserOne);
			recepients[1]=new InternetAddress(toUserTwo);
			message.addRecipients(Message.RecipientType.TO, recepients);
			message.setSubject("Bitcoin Price");
			String msg = "";
			for (Values val : data) {
				msg += "{ " + val.currencyType + "," + val.zebPay_Price + " }" + "\t";
			}
			message.setText(msg);
			Transport.send(message);
			System.out.println("message sent successfully....");

		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

	private static void printData() {
		for (int i = 0; i < data.size(); i++) {
			Values val = data.get(i);
			System.out.println(val.currencyType + "\t" + val.zebPay_Price);
		}
	}

	private static void fetchAndStorePrices() {
		List<WebElement> list = driver1.findElements(By.className("multicoin-price"));
		for (WebElement ele : list) {
			Values val = new Values();
			val.zebPay_Price = ele.getText().substring(1);
			val.currencyType = ele.getAttribute("id").substring(3).toUpperCase();
			data.add(val);
		}
	}

	private static void openCoinGecko() {
		driver2.get("https://www.coingecko.com/en");
		driver2.findElement(By.partialLinkText("INR")).click();
	}

	private static void openZebPay() throws InterruptedException {
		driver1.get("https://www.zebpay.com");
		Thread.sleep(2000);
		driver1.findElement(By.xpath("//a[@class='wmpci-popup-close']")).click();
	}
}
