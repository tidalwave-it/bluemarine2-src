
package it.tidalwave.bluemarine2.mediascanner;

import it.tidalwave.bluemarine2.model.impl.DefaultMediaFileSystem;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author fritz
 */
public class DefaultMediaScannerTest
  {
    private ClassPathXmlApplicationContext context;
    
    @BeforeMethod
    private void prepareTest()
      {
        final String s = "classpath:/META-INF/DefaultMediaScannerTestBeans.xml";
        context = new ClassPathXmlApplicationContext(s);
      }
    
    @Test
    public void testScan() 
      {
        final DefaultMediaScanner underTest = new DefaultMediaScanner();
        final DefaultMediaFileSystem mediaFileSystem = new DefaultMediaFileSystem();
        underTest.process(mediaFileSystem.getRoot());
      }
}
