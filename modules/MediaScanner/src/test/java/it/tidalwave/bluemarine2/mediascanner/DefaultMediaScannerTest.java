
package it.tidalwave.bluemarine2.mediascanner;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.n3.N3Writer;
import it.tidalwave.bluemarine2.model.impl.DefaultMediaFileSystem;
import it.tidalwave.util.test.FileComparisonUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author fritz
 */
@Slf4j
public class DefaultMediaScannerTest
  {
    private ClassPathXmlApplicationContext context;
    
    private Model model;
    
    @BeforeMethod
    private void prepareTest()
      {
        final String s = "classpath:/META-INF/DefaultMediaScannerTestBeans.xml";
        context = new ClassPathXmlApplicationContext(s);
        model = new LinkedHashModel()
          {
            @Override
            public boolean add (final @Nonnull Statement st) 
              {
                log.trace("STATEMENT: {}", st);
                return super.add(st); 
              }
          };
      }

    @Test
    public void testScan() 
      throws RDFHandlerException, IOException
      {
        final DefaultMediaScanner underTest = new DefaultMediaScanner(model);
        final DefaultMediaFileSystem mediaFileSystem = new DefaultMediaFileSystem();
        underTest.process(mediaFileSystem.getRoot());
        
        final File actualFile = new File("target/test-results/model.n3");
        final File expectedFile = new File("src/test/resources/expected-results/model.n3");
        dumpModel(actualFile);
        // FIXME: OOM
//        FileComparisonUtils.assertSameContents(expectedFile, actualFile);
      }
    
    private void dumpModel (final @Nonnull File file)
      throws RDFHandlerException, FileNotFoundException 
      {
        file.getParentFile().mkdirs();
        final PrintWriter pw = new PrintWriter(file);
        final N3Writer tw = new N3Writer(pw);
        tw.startRDF();
        model.stream().forEach(statement ->
          {
            try 
              {
                tw.handleStatement(statement);
              } 
            catch (RDFHandlerException e) 
              {
                throw new RuntimeException(e);
              }
          });
        
        tw.endRDF();
      }
  }
