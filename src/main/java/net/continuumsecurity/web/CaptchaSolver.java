package net.continuumsecurity.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.captcha.CaptchaSolverFactory;
import net.continuumsecurity.captcha.ISolveCaptcha;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

public class CaptchaSolver extends CaptchaFinder implements ICaptchaSolver {
	private ISolveCaptcha solver;
	public static Logger log = Logger.getLogger(CaptchaSolver.class);
	Properties properties;
	
	public CaptchaSolver(Application app, Properties properties) {
		super(app);
		this.properties = properties;
	}

	public void solve() {
		WebElement img = null;
		String solved = null;
		try {            
            solver = CaptchaSolverFactory.createSolver(properties);
            img = ((ICaptcha)app).getCaptchaImage();
			File screenshot = ((TakesScreenshot)app.getWebDriver()).getScreenshotAs(OutputType.FILE);
			Point point = img.getLocation();
			int eleWidth = img.getSize().getWidth();
			int eleHeight = img.getSize().getHeight();
			BufferedImage  fullImg = ImageIO.read(screenshot);
			//Crop the entire page screenshot to get only element screenshot
			BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
			ImageIO.write(eleScreenshot, "png", screenshot);
			FileUtils.copyFile(screenshot, new File("lastcaptcha.png"));
			solved = solver.solve(screenshot);
			if (solved != null) {
				log.debug("Solved CAPTCHA as: "+solved);
				((ICaptcha)app).getCaptchaResponseField().clear();
				((ICaptcha)app).getCaptchaResponseField().sendKeys(solved);
			}			
		} catch (NoSuchElementException nse) {
			log.debug("No CAPTCHA found, skipping.");
			return;
		} catch (Exception e) {
			log.error("Exception solving CAPTCHA: "+e.getMessage());
		}
		
	}
	

}
