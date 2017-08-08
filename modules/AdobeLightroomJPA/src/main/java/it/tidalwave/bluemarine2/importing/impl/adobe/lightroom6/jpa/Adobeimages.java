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
@Table(name = "Adobe_images")
@NamedQueries({
    @NamedQuery(name = "Adobeimages.findAll", query = "SELECT a FROM Adobeimages a"),
    @NamedQuery(name = "Adobeimages.findByIdLocal", query = "SELECT a FROM Adobeimages a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "Adobeimages.findByIdGlobal", query = "SELECT a FROM Adobeimages a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "Adobeimages.findByAspectRatioCache", query = "SELECT a FROM Adobeimages a WHERE a.aspectRatioCache = :aspectRatioCache"),
    @NamedQuery(name = "Adobeimages.findByBitDepth", query = "SELECT a FROM Adobeimages a WHERE a.bitDepth = :bitDepth"),
    @NamedQuery(name = "Adobeimages.findByCaptureTime", query = "SELECT a FROM Adobeimages a WHERE a.captureTime = :captureTime"),
    @NamedQuery(name = "Adobeimages.findByColorChannels", query = "SELECT a FROM Adobeimages a WHERE a.colorChannels = :colorChannels"),
    @NamedQuery(name = "Adobeimages.findByColorLabels", query = "SELECT a FROM Adobeimages a WHERE a.colorLabels = :colorLabels"),
    @NamedQuery(name = "Adobeimages.findByColorMode", query = "SELECT a FROM Adobeimages a WHERE a.colorMode = :colorMode"),
    @NamedQuery(name = "Adobeimages.findByCopyCreationTime", query = "SELECT a FROM Adobeimages a WHERE a.copyCreationTime = :copyCreationTime"),
    @NamedQuery(name = "Adobeimages.findByCopyName", query = "SELECT a FROM Adobeimages a WHERE a.copyName = :copyName"),
    @NamedQuery(name = "Adobeimages.findByCopyReason", query = "SELECT a FROM Adobeimages a WHERE a.copyReason = :copyReason"),
    @NamedQuery(name = "Adobeimages.findByDevelopSettingsIDCache", query = "SELECT a FROM Adobeimages a WHERE a.developSettingsIDCache = :developSettingsIDCache"),
    @NamedQuery(name = "Adobeimages.findByFileFormat", query = "SELECT a FROM Adobeimages a WHERE a.fileFormat = :fileFormat"),
    @NamedQuery(name = "Adobeimages.findByFileHeight", query = "SELECT a FROM Adobeimages a WHERE a.fileHeight = :fileHeight"),
    @NamedQuery(name = "Adobeimages.findByFileWidth", query = "SELECT a FROM Adobeimages a WHERE a.fileWidth = :fileWidth"),
    @NamedQuery(name = "Adobeimages.findByHasMissingSidecars", query = "SELECT a FROM Adobeimages a WHERE a.hasMissingSidecars = :hasMissingSidecars"),
    @NamedQuery(name = "Adobeimages.findByMasterImage", query = "SELECT a FROM Adobeimages a WHERE a.masterImage = :masterImage"),
    @NamedQuery(name = "Adobeimages.findByOrientation", query = "SELECT a FROM Adobeimages a WHERE a.orientation = :orientation"),
    @NamedQuery(name = "Adobeimages.findByOriginalCaptureTime", query = "SELECT a FROM Adobeimages a WHERE a.originalCaptureTime = :originalCaptureTime"),
    @NamedQuery(name = "Adobeimages.findByOriginalRootEntity", query = "SELECT a FROM Adobeimages a WHERE a.originalRootEntity = :originalRootEntity"),
    @NamedQuery(name = "Adobeimages.findByPanningDistanceH", query = "SELECT a FROM Adobeimages a WHERE a.panningDistanceH = :panningDistanceH"),
    @NamedQuery(name = "Adobeimages.findByPanningDistanceV", query = "SELECT a FROM Adobeimages a WHERE a.panningDistanceV = :panningDistanceV"),
    @NamedQuery(name = "Adobeimages.findByPick", query = "SELECT a FROM Adobeimages a WHERE a.pick = :pick"),
    @NamedQuery(name = "Adobeimages.findByPositionInFolder", query = "SELECT a FROM Adobeimages a WHERE a.positionInFolder = :positionInFolder"),
    @NamedQuery(name = "Adobeimages.findByPropertiesCache", query = "SELECT a FROM Adobeimages a WHERE a.propertiesCache = :propertiesCache"),
    @NamedQuery(name = "Adobeimages.findByPyramidIDCache", query = "SELECT a FROM Adobeimages a WHERE a.pyramidIDCache = :pyramidIDCache"),
    @NamedQuery(name = "Adobeimages.findByRating", query = "SELECT a FROM Adobeimages a WHERE a.rating = :rating"),
    @NamedQuery(name = "Adobeimages.findByRootFile", query = "SELECT a FROM Adobeimages a WHERE a.rootFile = :rootFile"),
    @NamedQuery(name = "Adobeimages.findBySidecarStatus", query = "SELECT a FROM Adobeimages a WHERE a.sidecarStatus = :sidecarStatus"),
    @NamedQuery(name = "Adobeimages.findByTouchCount", query = "SELECT a FROM Adobeimages a WHERE a.touchCount = :touchCount"),
    @NamedQuery(name = "Adobeimages.findByTouchTime", query = "SELECT a FROM Adobeimages a WHERE a.touchTime = :touchTime")})
public class Adobeimages implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Basic(optional = false)
    @Column(name = "aspectRatioCache")
    private String aspectRatioCache;
    @Basic(optional = false)
    @Column(name = "bitDepth")
    private String bitDepth;
    @Column(name = "captureTime")
    private String captureTime;
    @Basic(optional = false)
    @Column(name = "colorChannels")
    private String colorChannels;
    @Basic(optional = false)
    @Column(name = "colorLabels")
    private String colorLabels;
    @Basic(optional = false)
    @Column(name = "colorMode")
    private String colorMode;
    @Basic(optional = false)
    @Column(name = "copyCreationTime")
    private String copyCreationTime;
    @Column(name = "copyName")
    private String copyName;
    @Column(name = "copyReason")
    private String copyReason;
    @Column(name = "developSettingsIDCache")
    private String developSettingsIDCache;
    @Basic(optional = false)
    @Column(name = "fileFormat")
    private String fileFormat;
    @Column(name = "fileHeight")
    private String fileHeight;
    @Column(name = "fileWidth")
    private String fileWidth;
    @Column(name = "hasMissingSidecars")
    private Integer hasMissingSidecars;
    @Column(name = "masterImage")
    private Integer masterImage;
    @Column(name = "orientation")
    private String orientation;
    @Column(name = "originalCaptureTime")
    private String originalCaptureTime;
    @Column(name = "originalRootEntity")
    private Integer originalRootEntity;
    @Column(name = "panningDistanceH")
    private String panningDistanceH;
    @Column(name = "panningDistanceV")
    private String panningDistanceV;
    @Basic(optional = false)
    @Column(name = "pick")
    private String pick;
    @Basic(optional = false)
    @Column(name = "positionInFolder")
    private String positionInFolder;
    @Column(name = "propertiesCache")
    private String propertiesCache;
    @Column(name = "pyramidIDCache")
    private String pyramidIDCache;
    @Column(name = "rating")
    private String rating;
    @Basic(optional = false)
    @Column(name = "rootFile")
    private int rootFile;
    @Column(name = "sidecarStatus")
    private String sidecarStatus;
    @Basic(optional = false)
    @Column(name = "touchCount")
    private String touchCount;
    @Basic(optional = false)
    @Column(name = "touchTime")
    private String touchTime;

    public Adobeimages() {
    }

    public Adobeimages(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Adobeimages(Integer idLocal, String idGlobal, String aspectRatioCache, String bitDepth, String colorChannels, String colorLabels, String colorMode, String copyCreationTime, String fileFormat, String pick, String positionInFolder, int rootFile, String touchCount, String touchTime) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.aspectRatioCache = aspectRatioCache;
        this.bitDepth = bitDepth;
        this.colorChannels = colorChannels;
        this.colorLabels = colorLabels;
        this.colorMode = colorMode;
        this.copyCreationTime = copyCreationTime;
        this.fileFormat = fileFormat;
        this.pick = pick;
        this.positionInFolder = positionInFolder;
        this.rootFile = rootFile;
        this.touchCount = touchCount;
        this.touchTime = touchTime;
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

    public String getAspectRatioCache() {
        return aspectRatioCache;
    }

    public void setAspectRatioCache(String aspectRatioCache) {
        this.aspectRatioCache = aspectRatioCache;
    }

    public String getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(String bitDepth) {
        this.bitDepth = bitDepth;
    }

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }

    public String getColorChannels() {
        return colorChannels;
    }

    public void setColorChannels(String colorChannels) {
        this.colorChannels = colorChannels;
    }

    public String getColorLabels() {
        return colorLabels;
    }

    public void setColorLabels(String colorLabels) {
        this.colorLabels = colorLabels;
    }

    public String getColorMode() {
        return colorMode;
    }

    public void setColorMode(String colorMode) {
        this.colorMode = colorMode;
    }

    public String getCopyCreationTime() {
        return copyCreationTime;
    }

    public void setCopyCreationTime(String copyCreationTime) {
        this.copyCreationTime = copyCreationTime;
    }

    public String getCopyName() {
        return copyName;
    }

    public void setCopyName(String copyName) {
        this.copyName = copyName;
    }

    public String getCopyReason() {
        return copyReason;
    }

    public void setCopyReason(String copyReason) {
        this.copyReason = copyReason;
    }

    public String getDevelopSettingsIDCache() {
        return developSettingsIDCache;
    }

    public void setDevelopSettingsIDCache(String developSettingsIDCache) {
        this.developSettingsIDCache = developSettingsIDCache;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileHeight() {
        return fileHeight;
    }

    public void setFileHeight(String fileHeight) {
        this.fileHeight = fileHeight;
    }

    public String getFileWidth() {
        return fileWidth;
    }

    public void setFileWidth(String fileWidth) {
        this.fileWidth = fileWidth;
    }

    public Integer getHasMissingSidecars() {
        return hasMissingSidecars;
    }

    public void setHasMissingSidecars(Integer hasMissingSidecars) {
        this.hasMissingSidecars = hasMissingSidecars;
    }

    public Integer getMasterImage() {
        return masterImage;
    }

    public void setMasterImage(Integer masterImage) {
        this.masterImage = masterImage;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getOriginalCaptureTime() {
        return originalCaptureTime;
    }

    public void setOriginalCaptureTime(String originalCaptureTime) {
        this.originalCaptureTime = originalCaptureTime;
    }

    public Integer getOriginalRootEntity() {
        return originalRootEntity;
    }

    public void setOriginalRootEntity(Integer originalRootEntity) {
        this.originalRootEntity = originalRootEntity;
    }

    public String getPanningDistanceH() {
        return panningDistanceH;
    }

    public void setPanningDistanceH(String panningDistanceH) {
        this.panningDistanceH = panningDistanceH;
    }

    public String getPanningDistanceV() {
        return panningDistanceV;
    }

    public void setPanningDistanceV(String panningDistanceV) {
        this.panningDistanceV = panningDistanceV;
    }

    public String getPick() {
        return pick;
    }

    public void setPick(String pick) {
        this.pick = pick;
    }

    public String getPositionInFolder() {
        return positionInFolder;
    }

    public void setPositionInFolder(String positionInFolder) {
        this.positionInFolder = positionInFolder;
    }

    public String getPropertiesCache() {
        return propertiesCache;
    }

    public void setPropertiesCache(String propertiesCache) {
        this.propertiesCache = propertiesCache;
    }

    public String getPyramidIDCache() {
        return pyramidIDCache;
    }

    public void setPyramidIDCache(String pyramidIDCache) {
        this.pyramidIDCache = pyramidIDCache;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getRootFile() {
        return rootFile;
    }

    public void setRootFile(int rootFile) {
        this.rootFile = rootFile;
    }

    public String getSidecarStatus() {
        return sidecarStatus;
    }

    public void setSidecarStatus(String sidecarStatus) {
        this.sidecarStatus = sidecarStatus;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLocal != null ? idLocal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Adobeimages)) {
            return false;
        }
        Adobeimages other = (Adobeimages) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.Adobeimages[ idLocal=" + idLocal + " ]";
    }
  }
