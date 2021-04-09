/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.captureone.impl.applescript;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import it.tidalwave.bluemarine2.captureone.CaptureOneDao;
import it.tidalwave.bluemarine2.captureone.impl.C1Collection;
import it.tidalwave.bluemarine2.captureone.impl.C1Document;
import it.tidalwave.bluemarine2.captureone.impl.C1Image;
import static java.nio.charset.StandardCharsets.UTF_8;

/***********************************************************************************************************************
 *
 * An implementation of {@link CaptureOneDao} based on Applescript.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class AppleScriptCaptureOneDao implements CaptureOneDao
  {
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public C1Document getDocument()
      throws IOException
      {
        try
          {
            return parse(getDocumentAsXml());
          }
        catch (IOException | ScriptException | JAXBException e)
          {
            throw new IOException(e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    /* package */ C1Document parse (final @Nonnull String xml)
      throws JAXBException
      {
        final JAXBContext context = JAXBContext.newInstance(C1Document.class, C1Collection.class, C1Image.class);
        return (C1Document)context.createUnmarshaller().unmarshal(new StringReader(xml));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    /* package */ String getDocumentAsXml()
      throws IOException, ScriptException
      {
        return execute("Capture One.scpt");
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    /* package */ String getTestAsXml()
      throws IOException, ScriptException
      {
        return execute("Test.scpt");
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String execute (final String scriptName)
      throws IOException, ScriptException
      {
        final ScriptEngine se = createEngine();

        try (final Reader sr = new InputStreamReader(AppleScriptCaptureOneDao.class.getResourceAsStream(scriptName), UTF_8))
          {
            return se.eval(sr, se.getContext()).toString();
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static ScriptEngine createEngine()
      {
        final ScriptEngineManager sem = new ScriptEngineManager();
        final ScriptEngine se = sem.getEngineByName("AppleScript");
        final ScriptContext context = se.getContext();
        context.setErrorWriter(new PrintWriter(System.err, true));
        context.setWriter(new PrintWriter(System.out));
        return se;
      }
  }
