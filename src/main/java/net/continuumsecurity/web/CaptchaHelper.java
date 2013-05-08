package net.continuumsecurity.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.caption.ISolveCaptcha;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

public class CaptchaHelper extends CaptchaFinder implements ICaptchaHelper {
	private ISolveCaptcha solver;
	public static Logger log = Logger.getLogger(CaptchaHelper.class);
	
	public CaptchaHelper(Application app,ISolveCaptcha solver) {
		super(app);
		this.solver = solver;
	}

	public void solve() {
		WebElement img = null;
		String solved = null;
		try {
			img = ((ICaptcha)app).getCaptchaImage();
			File screenshot = ((TakesScreenshot)app.getWebDriver()).getScreenshotAs(OutputType.FILE);
			BufferedImage  fullImg = ImageIO.read(screenshot);
			//Get the location of element on the page
			Point point = img.getLocation();
			//Get width and height of the element
			int eleWidth = img.getSize().getWidth();
			int eleHeight = img.getSize().getHeight();
			//Crop the entire page screenshot to get only element screenshot
			BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
			ImageIO.write(eleScreenshot, "png", screenshot);
			FileUtils.copyFile(screenshot, new File("lastcaptcha.png"));
			solved = solver.solveFromFile(screenshot);	
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
	
	/*
	 * Makes another request to for the image and solves it.  Deprecated in preference for the screenshot method.
	 */
	public void solveThroughUrl() {
		WebElement img = null;
		String solved = null;
		try {
			img = ((ICaptcha)app).getCaptchaImage();
			solved = solver.solveFromUrl(img.getAttribute("src"));
		} catch (NoSuchElementException nse) {
			log.info("No CAPTCHA found, skipping.");
			return;
		} catch (Exception e) {
			log.error("Exception solving CAPTCHA: "+e.getMessage());
			e.printStackTrace();
			return;		
		}
		if (solved != null) {
			((ICaptcha)app).getCaptchaResponseField().sendKeys(solved);
		}
	}
	

}
