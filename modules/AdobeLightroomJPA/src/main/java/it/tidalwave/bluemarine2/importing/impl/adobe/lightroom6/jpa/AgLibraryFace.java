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
@Table(name = "AgLibraryFace")
@NamedQueries({
    @NamedQuery(name = "AgLibraryFace.findAll", query = "SELECT a FROM AgLibraryFace a"),
    @NamedQuery(name = "AgLibraryFace.findByIdLocal", query = "SELECT a FROM AgLibraryFace a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryFace.findByBlX", query = "SELECT a FROM AgLibraryFace a WHERE a.blX = :blX"),
    @NamedQuery(name = "AgLibraryFace.findByBlY", query = "SELECT a FROM AgLibraryFace a WHERE a.blY = :blY"),
    @NamedQuery(name = "AgLibraryFace.findByBrX", query = "SELECT a FROM AgLibraryFace a WHERE a.brX = :brX"),
    @NamedQuery(name = "AgLibraryFace.findByBrY", query = "SELECT a FROM AgLibraryFace a WHERE a.brY = :brY"),
    @NamedQuery(name = "AgLibraryFace.findByCluster", query = "SELECT a FROM AgLibraryFace a WHERE a.cluster = :cluster"),
    @NamedQuery(name = "AgLibraryFace.findByCompatibleVersion", query = "SELECT a FROM AgLibraryFace a WHERE a.compatibleVersion = :compatibleVersion"),
    @NamedQuery(name = "AgLibraryFace.findByIgnored", query = "SELECT a FROM AgLibraryFace a WHERE a.ignored = :ignored"),
    @NamedQuery(name = "AgLibraryFace.findByImage", query = "SELECT a FROM AgLibraryFace a WHERE a.image = :image"),
    @NamedQuery(name = "AgLibraryFace.findByImageOrientation", query = "SELECT a FROM AgLibraryFace a WHERE a.imageOrientation = :imageOrientation"),
    @NamedQuery(name = "AgLibraryFace.findByOrientation", query = "SELECT a FROM AgLibraryFace a WHERE a.orientation = :orientation"),
    @NamedQuery(name = "AgLibraryFace.findByOrigination", query = "SELECT a FROM AgLibraryFace a WHERE a.origination = :origination"),
    @NamedQuery(name = "AgLibraryFace.findByPropertiesCache", query = "SELECT a FROM AgLibraryFace a WHERE a.propertiesCache = :propertiesCache"),
    @NamedQuery(name = "AgLibraryFace.findByRegionType", query = "SELECT a FROM AgLibraryFace a WHERE a.regionType = :regionType"),
    @NamedQuery(name = "AgLibraryFace.findBySkipSuggestion", query = "SELECT a FROM AgLibraryFace a WHERE a.skipSuggestion = :skipSuggestion"),
    @NamedQuery(name = "AgLibraryFace.findByTlX", query = "SELECT a FROM AgLibraryFace a WHERE a.tlX = :tlX"),
    @NamedQuery(name = "AgLibraryFace.findByTlY", query = "SELECT a FROM AgLibraryFace a WHERE a.tlY = :tlY"),
    @NamedQuery(name = "AgLibraryFace.findByTouchCount", query = "SELECT a FROM AgLibraryFace a WHERE a.touchCount = :touchCount"),
    @NamedQuery(name = "AgLibraryFace.findByTouchTime", query = "SELECT a FROM AgLibraryFace a WHERE a.touchTime = :touchTime"),
    @NamedQuery(name = "AgLibraryFace.findByTrX", query = "SELECT a FROM AgLibraryFace a WHERE a.trX = :trX"),
    @NamedQuery(name = "AgLibraryFace.findByTrY", query = "SELECT a FROM AgLibraryFace a WHERE a.trY = :trY"),
    @NamedQuery(name = "AgLibraryFace.findByVersion", query = "SELECT a FROM AgLibraryFace a WHERE a.version = :version")})
public class AgLibraryFace implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "bl_x")
    private String blX;
    @Column(name = "bl_y")
    private String blY;
    @Column(name = "br_x")
    private String brX;
    @Column(name = "br_y")
    private String brY;
    @Column(name = "cluster")
    private Integer cluster;
    @Column(name = "compatibleVersion")
    private String compatibleVersion;
    @Column(name = "ignored")
    private Integer ignored;
    @Basic(optional = false)
    @Column(name = "image")
    private int image;
    @Basic(optional = false)
    @Column(name = "imageOrientation")
    private String imageOrientation;
    @Column(name = "orientation")
    private String orientation;
    @Basic(optional = false)
    @Column(name = "origination")
    private String origination;
    @Column(name = "propertiesCache")
    private String propertiesCache;
    @Basic(optional = false)
    @Column(name = "regionType")
    private String regionType;
    @Column(name = "skipSuggestion")
    private Integer skipSuggestion;
    @Basic(optional = false)
    @Column(name = "tl_x")
    private String tlX;
    @Basic(optional = false)
    @Column(name = "tl_y")
    private String tlY;
    @Basic(optional = false)
    @Column(name = "touchCount")
    private String touchCount;
    @Basic(optional = false)
    @Column(name = "touchTime")
    private String touchTime;
    @Column(name = "tr_x")
    private String trX;
    @Column(name = "tr_y")
    private String trY;
    @Column(name = "version")
    private String version;

    public AgLibraryFace() {
    }

    public AgLibraryFace(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryFace(Integer idLocal, int image, String imageOrientation, String origination, String regionType, String tlX, String tlY, String touchCount, String touchTime) {
        this.idLocal = idLocal;
        this.image = image;
        this.imageOrientation = imageOrientation;
        this.origination = origination;
        this.regionType = regionType;
        this.tlX = tlX;
        this.tlY = tlY;
        this.touchCount = touchCount;
        this.touchTime = touchTime;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getBlX() {
        return blX;
    }

    public void setBlX(String blX) {
        this.blX = blX;
    }

    public String getBlY() {
        return blY;
    }

    public void setBlY(String blY) {
        this.blY = blY;
    }

    public String getBrX() {
        return brX;
    }

    public void setBrX(String brX) {
        this.brX = brX;
    }

    public String getBrY() {
        return brY;
    }

    public void setBrY(String brY) {
        this.brY = brY;
    }

    public Integer getCluster() {
        return cluster;
    }

    public void setCluster(Integer cluster) {
        this.cluster = cluster;
    }

    public String getCompatibleVersion() {
        return compatibleVersion;
    }

    public void setCompatibleVersion(String compatibleVersion) {
        this.compatibleVersion = compatibleVersion;
    }

    public Integer getIgnored() {
        return ignored;
    }

    public void setIgnored(Integer ignored) {
        this.ignored = ignored;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getImageOrientation() {
        return imageOrientation;
    }

    public void setImageOrientation(String imageOrientation) {
        this.imageOrientation = imageOrientation;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getOrigination() {
        return origination;
    }

    public void setOrigination(String origination) {
        this.origination = origination;
    }

    public String getPropertiesCache() {
        return propertiesCache;
    }

    public void setPropertiesCache(String propertiesCache) {
        this.propertiesCache = propertiesCache;
    }

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    public Integer getSkipSuggestion() {
        return skipSuggestion;
    }

    public void setSkipSuggestion(Integer skipSuggestion) {
        this.skipSuggestion = skipSuggestion;
    }

    public String getTlX() {
        return tlX;
    }

    public void setTlX(String tlX) {
        this.tlX = tlX;
    }

    public String getTlY() {
        return tlY;
    }

    public void setTlY(String tlY) {
        this.tlY = tlY;
    }

    public String getTouchCount() {
        return touchCount;
    }

    public void setTouchCount(String touchCount) {
        this.touchCount = touchCount;
    }

    public String getTouchTime() {
        return touchTime;
    }

    public void setTouchTime(String touchTime) {
        this.touchTime = touchTime;
    }

    public String getTrX() {
        return trX;
    }

    public void setTrX(String trX) {
        this.trX = trX;
    }

    public String getTrY() {
        return trY;
    }

    public void setTrY(String trY) {
        this.trY = trY;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
        if (!(object instanceof AgLibraryFace)) {
            return false;
        }
        AgLibraryFace other = (AgLibraryFace) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryFace[ idLocal=" + idLocal + " ]";
    }
  }
