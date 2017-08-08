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
@Table(name = "Adobe_libraryImageFaceProcessHistory")
@NamedQueries({
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findAll", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a"),
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findByIdLocal", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findByImage", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a WHERE a.image = :image"),
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findByLastFaceDetector", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a WHERE a.lastFaceDetector = :lastFaceDetector"),
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findByLastFaceRecognizer", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a WHERE a.lastFaceRecognizer = :lastFaceRecognizer"),
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findByLastImageIndexer", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a WHERE a.lastImageIndexer = :lastImageIndexer"),
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findByLastImageOrientation", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a WHERE a.lastImageOrientation = :lastImageOrientation"),
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findByLastTryStatus", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a WHERE a.lastTryStatus = :lastTryStatus"),
    @NamedQuery(name = "AdobelibraryImageFaceProcessHistory.findByUserTouched", query = "SELECT a FROM AdobelibraryImageFaceProcessHistory a WHERE a.userTouched = :userTouched")})
public class AdobelibraryImageFaceProcessHistory implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "image")
    private int image;
    @Column(name = "lastFaceDetector")
    private String lastFaceDetector;
    @Column(name = "lastFaceRecognizer")
    private String lastFaceRecognizer;
    @Column(name = "lastImageIndexer")
    private String lastImageIndexer;
    @Column(name = "lastImageOrientation")
    private String lastImageOrientation;
    @Column(name = "lastTryStatus")
    private String lastTryStatus;
    @Column(name = "userTouched")
    private String userTouched;

    public AdobelibraryImageFaceProcessHistory() {
    }

    public AdobelibraryImageFaceProcessHistory(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AdobelibraryImageFaceProcessHistory(Integer idLocal, int image) {
        this.idLocal = idLocal;
        this.image = image;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLastFaceDetector() {
        return lastFaceDetector;
    }

    public void setLastFaceDetector(String lastFaceDetector) {
        this.lastFaceDetector = lastFaceDetector;
    }

    public String getLastFaceRecognizer() {
        return lastFaceRecognizer;
    }

    public void setLastFaceRecognizer(String lastFaceRecognizer) {
        this.lastFaceRecognizer = lastFaceRecognizer;
    }

    public String getLastImageIndexer() {
        return lastImageIndexer;
    }

    public void setLastImageIndexer(String lastImageIndexer) {
        this.lastImageIndexer = lastImageIndexer;
    }

    public String getLastImageOrientation() {
        return lastImageOrientation;
    }

    public void setLastImageOrientation(String lastImageOrientation) {
        this.lastImageOrientation = lastImageOrientation;
    }

    public String getLastTryStatus() {
        return lastTryStatus;
    }

    public void setLastTryStatus(String lastTryStatus) {
        this.lastTryStatus = lastTryStatus;
    }

    public String getUserTouched() {
        return userTouched;
    }

    public void setUserTouched(String userTouched) {
        this.userTouched = userTouched;
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
        if (!(object instanceof AdobelibraryImageFaceProcessHistory)) {
            return false;
        }
        AdobelibraryImageFaceProcessHistory other = (AdobelibraryImageFaceProcessHistory) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobelibraryImageFaceProcessHistory[ idLocal=" + idLocal + " ]";
    }
  }
