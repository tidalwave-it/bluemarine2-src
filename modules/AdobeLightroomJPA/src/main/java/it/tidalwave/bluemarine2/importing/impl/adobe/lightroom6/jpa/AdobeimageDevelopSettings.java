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
@Table(name = "Adobe_imageDevelopSettings")
@NamedQueries({
    @NamedQuery(name = "AdobeimageDevelopSettings.findAll", query = "SELECT a FROM AdobeimageDevelopSettings a"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByIdLocal", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByAllowFastRender", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.allowFastRender = :allowFastRender"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByBeforeSettingsIDCache", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.beforeSettingsIDCache = :beforeSettingsIDCache"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByCroppedHeight", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.croppedHeight = :croppedHeight"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByCroppedWidth", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.croppedWidth = :croppedWidth"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByDigest", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.digest = :digest"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByFileHeight", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.fileHeight = :fileHeight"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByFileWidth", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.fileWidth = :fileWidth"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByGrayscale", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.grayscale = :grayscale"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByHasDevelopAdjustments", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.hasDevelopAdjustments = :hasDevelopAdjustments"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByHasDevelopAdjustmentsEx", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.hasDevelopAdjustmentsEx = :hasDevelopAdjustmentsEx"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByHistorySettingsID", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.historySettingsID = :historySettingsID"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByImage", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.image = :image"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByProcessVersion", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.processVersion = :processVersion"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findBySettingsID", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.settingsID = :settingsID"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findBySnapshotID", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.snapshotID = :snapshotID"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByText", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.text = :text"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByValidatedForVersion", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.validatedForVersion = :validatedForVersion"),
    @NamedQuery(name = "AdobeimageDevelopSettings.findByWhiteBalance", query = "SELECT a FROM AdobeimageDevelopSettings a WHERE a.whiteBalance = :whiteBalance")})
public class AdobeimageDevelopSettings implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "allowFastRender")
    private Integer allowFastRender;
    @Column(name = "beforeSettingsIDCache")
    private String beforeSettingsIDCache;
    @Column(name = "croppedHeight")
    private String croppedHeight;
    @Column(name = "croppedWidth")
    private String croppedWidth;
    @Column(name = "digest")
    private String digest;
    @Column(name = "fileHeight")
    private String fileHeight;
    @Column(name = "fileWidth")
    private String fileWidth;
    @Column(name = "grayscale")
    private Integer grayscale;
    @Column(name = "hasDevelopAdjustments")
    private Integer hasDevelopAdjustments;
    @Column(name = "hasDevelopAdjustmentsEx")
    private String hasDevelopAdjustmentsEx;
    @Column(name = "historySettingsID")
    private String historySettingsID;
    @Column(name = "image")
    private Integer image;
    @Column(name = "processVersion")
    private String processVersion;
    @Column(name = "settingsID")
    private String settingsID;
    @Column(name = "snapshotID")
    private String snapshotID;
    @Column(name = "text")
    private String text;
    @Column(name = "validatedForVersion")
    private String validatedForVersion;
    @Column(name = "whiteBalance")
    private String whiteBalance;

    public AdobeimageDevelopSettings() {
    }

    public AdobeimageDevelopSettings(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getAllowFastRender() {
        return allowFastRender;
    }

    public void setAllowFastRender(Integer allowFastRender) {
        this.allowFastRender = allowFastRender;
    }

    public String getBeforeSettingsIDCache() {
        return beforeSettingsIDCache;
    }

    public void setBeforeSettingsIDCache(String beforeSettingsIDCache) {
        this.beforeSettingsIDCache = beforeSettingsIDCache;
    }

    public String getCroppedHeight() {
        return croppedHeight;
    }

    public void setCroppedHeight(String croppedHeight) {
        this.croppedHeight = croppedHeight;
    }

    public String getCroppedWidth() {
        return croppedWidth;
    }

    public void setCroppedWidth(String croppedWidth) {
        this.croppedWidth = croppedWidth;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
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

    public Integer getGrayscale() {
        return grayscale;
    }

    public void setGrayscale(Integer grayscale) {
        this.grayscale = grayscale;
    }

    public Integer getHasDevelopAdjustments() {
        return hasDevelopAdjustments;
    }

    public void setHasDevelopAdjustments(Integer hasDevelopAdjustments) {
        this.hasDevelopAdjustments = hasDevelopAdjustments;
    }

    public String getHasDevelopAdjustmentsEx() {
        return hasDevelopAdjustmentsEx;
    }

    public void setHasDevelopAdjustmentsEx(String hasDevelopAdjustmentsEx) {
        this.hasDevelopAdjustmentsEx = hasDevelopAdjustmentsEx;
    }

    public String getHistorySettingsID() {
        return historySettingsID;
    }

    public void setHistorySettingsID(String historySettingsID) {
        this.historySettingsID = historySettingsID;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getSettingsID() {
        return settingsID;
    }

    public void setSettingsID(String settingsID) {
        this.settingsID = settingsID;
    }

    public String getSnapshotID() {
        return snapshotID;
    }

    public void setSnapshotID(String snapshotID) {
        this.snapshotID = snapshotID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValidatedForVersion() {
        return validatedForVersion;
    }

    public void setValidatedForVersion(String validatedForVersion) {
        this.validatedForVersion = validatedForVersion;
    }

    public String getWhiteBalance() {
        return whiteBalance;
    }

    public void setWhiteBalance(String whiteBalance) {
        this.whiteBalance = whiteBalance;
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
        if (!(object instanceof AdobeimageDevelopSettings)) {
            return false;
        }
        AdobeimageDevelopSettings other = (AdobeimageDevelopSettings) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobeimageDevelopSettings[ idLocal=" + idLocal + " ]";
    }
  }
