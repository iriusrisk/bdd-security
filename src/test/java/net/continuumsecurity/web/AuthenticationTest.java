package java.net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.WebApplicationSteps;
public class AuthenticationTest {
  public WebApplicationSteps webAppSteps = new WebApplicationSteps();
  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
  }

  @Test
  public void password_should_be_case_sensitive(){
  
  }
}
