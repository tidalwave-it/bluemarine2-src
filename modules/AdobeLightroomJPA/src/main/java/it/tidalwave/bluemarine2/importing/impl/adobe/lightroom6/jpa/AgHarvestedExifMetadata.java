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
@Table(name = "AgHarvestedExifMetadata")
@NamedQueries({
    @NamedQuery(name = "AgHarvestedExifMetadata.findAll", query = "SELECT a FROM AgHarvestedExifMetadata a"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByIdLocal", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByImage", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.image = :image"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByAperture", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.aperture = :aperture"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByCameraModelRef", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.cameraModelRef = :cameraModelRef"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByCameraSNRef", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.cameraSNRef = :cameraSNRef"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByDateDay", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.dateDay = :dateDay"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByDateMonth", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.dateMonth = :dateMonth"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByDateYear", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.dateYear = :dateYear"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByFlashFired", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.flashFired = :flashFired"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByFocalLength", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.focalLength = :focalLength"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByGpsLatitude", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.gpsLatitude = :gpsLatitude"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByGpsLongitude", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.gpsLongitude = :gpsLongitude"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByGpsSequence", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.gpsSequence = :gpsSequence"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByHasGPS", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.hasGPS = :hasGPS"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByIsoSpeedRating", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.isoSpeedRating = :isoSpeedRating"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByLensRef", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.lensRef = :lensRef"),
    @NamedQuery(name = "AgHarvestedExifMetadata.findByShutterSpeed", query = "SELECT a FROM AgHarvestedExifMetadata a WHERE a.shutterSpeed = :shutterSpeed")})
public class AgHarvestedExifMetadata implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "image")
    private Integer image;
    @Column(name = "aperture")
    private String aperture;
    @Column(name = "cameraModelRef")
    private Integer cameraModelRef;
    @Column(name = "cameraSNRef")
    private Integer cameraSNRef;
    @Column(name = "dateDay")
    private String dateDay;
    @Column(name = "dateMonth")
    private String dateMonth;
    @Column(name = "dateYear")
    private String dateYear;
    @Column(name = "flashFired")
    private Integer flashFired;
    @Column(name = "focalLength")
    private String focalLength;
    @Column(name = "gpsLatitude")
    private String gpsLatitude;
    @Column(name = "gpsLongitude")
    private String gpsLongitude;
    @Basic(optional = false)
    @Column(name = "gpsSequence")
    private String gpsSequence;
    @Column(name = "hasGPS")
    private Integer hasGPS;
    @Column(name = "isoSpeedRating")
    private String isoSpeedRating;
    @Column(name = "lensRef")
    private Integer lensRef;
    @Column(name = "shutterSpeed")
    private String shutterSpeed;

    public AgHarvestedExifMetadata() {
    }

    public AgHarvestedExifMetadata(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgHarvestedExifMetadata(Integer idLocal, String gpsSequence) {
        this.idLocal = idLocal;
        this.gpsSequence = gpsSequence;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public Integer getCameraModelRef() {
        return cameraModelRef;
    }

    public void setCameraModelRef(Integer cameraModelRef) {
        this.cameraModelRef = cameraModelRef;
    }

    public Integer getCameraSNRef() {
        return cameraSNRef;
    }

    public void setCameraSNRef(Integer cameraSNRef) {
        this.cameraSNRef = cameraSNRef;
    }

    public String getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(String dateMonth) {
        this.dateMonth = dateMonth;
    }

    public String getDateYear() {
        return dateYear;
    }

    public void setDateYear(String dateYear) {
        this.dateYear = dateYear;
    }

    public Integer getFlashFired() {
        return flashFired;
    }

    public void setFlashFired(Integer flashFired) {
        this.flashFired = flashFired;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public String getGpsSequence() {
        return gpsSequence;
    }

    public void setGpsSequence(String gpsSequence) {
        this.gpsSequence = gpsSequence;
    }

    public Integer getHasGPS() {
        return hasGPS;
    }

    public void setHasGPS(Integer hasGPS) {
        this.hasGPS = hasGPS;
    }

    public String getIsoSpeedRating() {
        return isoSpeedRating;
    }

    public void setIsoSpeedRating(String isoSpeedRating) {
        this.isoSpeedRating = isoSpeedRating;
    }

    public Integer getLensRef() {
        return lensRef;
    }

    public void setLensRef(Integer lensRef) {
        this.lensRef = lensRef;
    }

    public String getShutterSpeed() {
        return shutterSpeed;
    }

    public void setShutterSpeed(String shutterSpeed) {
        this.shutterSpeed = shutterSpeed;
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
        if (!(object instanceof AgHarvestedExifMetadata)) {
            return false;
        }
        AgHarvestedExifMetadata other = (AgHarvestedExifMetadata) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgHarvestedExifMetadata[ idLocal=" + idLocal + " ]";
    }
  }
