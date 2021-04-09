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
package it.tidalwave.bluemarine2.captureone.impl;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * An image in Capture One. It is described by a name, a path and an id.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@XmlRootElement(name = "photo")
@XmlAccessorType(XmlAccessType.NONE)
@RequiredArgsConstructor @Getter @ToString @EqualsAndHashCode
public class C1Image
  {
    @XmlElement(name = "name") @Nonnull
    private final String name;

    @XmlElement(name = "path") @Nonnull
    private final String path;

    @XmlElement(name = "id") @Nonnull
    private final String id;

    @SuppressWarnings("unused")
    private C1Image()
      {
        this("", "", "");
      }
  }
