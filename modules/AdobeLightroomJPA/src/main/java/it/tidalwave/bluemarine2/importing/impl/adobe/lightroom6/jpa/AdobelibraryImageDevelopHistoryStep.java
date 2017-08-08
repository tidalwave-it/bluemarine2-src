/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
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
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.importing.impl.adobe.lightroom6.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id:$
 *
 **********************************************************************************************************************/
@Entity
@Table(name = "Adobe_libraryImageDevelopHistoryStep")
@NamedQueries({
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findAll", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByIdLocal", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByIdGlobal", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByDateCreated", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.dateCreated = :dateCreated"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByDigest", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.digest = :digest"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByHasDevelopAdjustments", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.hasDevelopAdjustments = :hasDevelopAdjustments"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByImage", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.image = :image"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByName", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.name = :name"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByRelValueString", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.relValueString = :relValueString"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByText", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.text = :text"),
    @NamedQuery(name = "AdobelibraryImageDevelopHistoryStep.findByValueString", query = "SELECT a FROM AdobelibraryImageDevelopHistoryStep a WHERE a.valueString = :valueString")})
public class AdobelibraryImageDevelopHistoryStep implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Column(name = "dateCreated")
    private String dateCreated;
    @Column(name = "digest")
    private String digest;
    @Column(name = "hasDevelopAdjustments")
    private String hasDevelopAdjustments;
    @Column(name = "image")
    private Integer image;
    @Column(name = "name")
    private String name;
    @Column(name = "relValueString")
    private String relValueString;
    @Column(name = "text")
    private String text;
    @Column(name = "valueString")
    private String valueString;

    public AdobelibraryImageDevelopHistoryStep() {
    }

    public AdobelibraryImageDevelopHistoryStep(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AdobelibraryImageDevelopHistoryStep(Integer idLocal, String idGlobal) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getIdGlobal() {
        return idGlobal;
    }

    public void setIdGlobal(String idGlobal) {
        this.idGlobal = idGlobal;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getHasDevelopAdjustments() {
        return hasDevelopAdjustments;
    }

    public void setHasDevelopAdjustments(String hasDevelopAdjustments) {
        this.hasDevelopAdjustments = hasDevelopAdjustments;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelValueString() {
        return relValueString;
    }

    public void setRelValueString(String relValueString) {
        this.relValueString = relValueString;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLocal != null ? idLocal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AdobelibraryImageDevelopHistoryStep)) {
            return false;
        }
        AdobelibraryImageDevelopHistoryStep other = (AdobelibraryImageDevelopHistoryStep) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobelibraryImageDevelopHistoryStep[ idLocal=" + idLocal + " ]";
    }
  }
