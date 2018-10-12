package net.continuumsecurity;

import net.continuumsecurity.behaviour.INavigable;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.By;

public class MyComplexApp extends WebApplication implements INavigable {

//	@Override
//    public void openLoginPage() {
//       driver.get(Config.getInstance().getBaseUrl());
//       verifyTextPresent("Login");
//    }
//
//    @Override
//    public void login(Credentials credentials) {
//       UserPassCredentials creds = new UserPassCredentials(credentials);
//       driver.findElement(By.id("username")).clear();
//       driver.findElement(By.id("username")).sendKeys(creds.getUsername());
//       driver.findElement(By.id("password")).clear();
//       driver.findElement(By.id("password")).sendKeys(creds.getPassword());
//       driver.findElement(By.id("submit")).click();
//    }
//
//    @Override
//    public boolean isLoggedIn() {
//       if (driver.getPageSource().contains("Tasks")) {
//           return true;
//       } else {
//           return false;
//       }
//    }
//
//    public void navigate() {
//       openLoginPage();
//       login(Config.getInstance().getDefaultCredentials());
//       //navigate the app
//    }
//
//	@Override
//	public void logout() {
//		// TODO Auto-generated method stub
//		
//	}
	
    public void navigate() {
        driver.get(Config.getInstance().getBaseUrl());
 //       if (driver.findElement(By.className("igo-login-message")).getText().contains("Effective Leaders Effective Action")) {
	// 	driver.findElement(By.id("username")).clear();
	// 	sleep(1000);
	// 	driver.findElement(By.id("username")).sendKeys("user");
	// 	driver.findElement(By.id("password")).clear();
	// 	sleep(1000);
	// 	driver.findElement(By.id("password")).sendKeys("password");
	// 	sleep(1000);
	// 	driver.findElement(By.xpath("//button[contains(text(),'Sign In')]")).click();
	// 	sleep(3000);
	// }
        
        /*UserPassCredentials creds = new UserPassCredentials(Config.getInstance().getDefaultCredentials());
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(creds.getUsername());
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(creds.getPassword());
        driver.findElement(By.name("_action_login")).click();

        //Click on the "tasks" link
        findAndWaitForElement(By.linkText("Tasks")).click();

        //Enter a search query
        driver.findElement(By.id("q")).clear();
        driver.findElement(By.id("q")).sendKeys("test");
        driver.findElement(By.id("search")).click();*/
        
        
    }
    
    
    private void sleep(long sleep) {
    	try {
        	Thread.sleep(sleep);
        } catch (InterruptedException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }

	}
}
