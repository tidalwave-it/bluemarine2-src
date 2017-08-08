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
@Table(name = "Adobe_AdditionalMetadata")
@NamedQueries({
    @NamedQuery(name = "AdobeAdditionalMetadata.findAll", query = "SELECT a FROM AdobeAdditionalMetadata a"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByIdLocal", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByIdGlobal", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByAdditionalInfoSet", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.additionalInfoSet = :additionalInfoSet"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByEmbeddedXmp", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.embeddedXmp = :embeddedXmp"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByExternalXmpIsDirty", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.externalXmpIsDirty = :externalXmpIsDirty"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByImage", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.image = :image"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByIncrementalWhiteBalance", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.incrementalWhiteBalance = :incrementalWhiteBalance"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByInternalXmpDigest", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.internalXmpDigest = :internalXmpDigest"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByIsRawFile", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.isRawFile = :isRawFile"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByLastSynchronizedHash", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.lastSynchronizedHash = :lastSynchronizedHash"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByLastSynchronizedTimestamp", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.lastSynchronizedTimestamp = :lastSynchronizedTimestamp"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByMetadataPresetID", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.metadataPresetID = :metadataPresetID"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByMetadataVersion", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.metadataVersion = :metadataVersion"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByMonochrome", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.monochrome = :monochrome"),
    @NamedQuery(name = "AdobeAdditionalMetadata.findByXmp", query = "SELECT a FROM AdobeAdditionalMetadata a WHERE a.xmp = :xmp")})
public class AdobeAdditionalMetadata implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Basic(optional = false)
    @Column(name = "additionalInfoSet")
    private int additionalInfoSet;
    @Basic(optional = false)
    @Column(name = "embeddedXmp")
    private int embeddedXmp;
    @Basic(optional = false)
    @Column(name = "externalXmpIsDirty")
    private int externalXmpIsDirty;
    @Column(name = "image")
    private Integer image;
    @Basic(optional = false)
    @Column(name = "incrementalWhiteBalance")
    private int incrementalWhiteBalance;
    @Column(name = "internalXmpDigest")
    private String internalXmpDigest;
    @Basic(optional = false)
    @Column(name = "isRawFile")
    private int isRawFile;
    @Column(name = "lastSynchronizedHash")
    private String lastSynchronizedHash;
    @Basic(optional = false)
    @Column(name = "lastSynchronizedTimestamp")
    private String lastSynchronizedTimestamp;
    @Column(name = "metadataPresetID")
    private String metadataPresetID;
    @Column(name = "metadataVersion")
    private String metadataVersion;
    @Basic(optional = false)
    @Column(name = "monochrome")
    private int monochrome;
    @Basic(optional = false)
    @Column(name = "xmp")
    private String xmp;

    public AdobeAdditionalMetadata() {
    }

    public AdobeAdditionalMetadata(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AdobeAdditionalMetadata(Integer idLocal, String idGlobal, int additionalInfoSet, int embeddedXmp, int externalXmpIsDirty, int incrementalWhiteBalance, int isRawFile, String lastSynchronizedTimestamp, int monochrome, String xmp) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.additionalInfoSet = additionalInfoSet;
        this.embeddedXmp = embeddedXmp;
        this.externalXmpIsDirty = externalXmpIsDirty;
        this.incrementalWhiteBalance = incrementalWhiteBalance;
        this.isRawFile = isRawFile;
        this.lastSynchronizedTimestamp = lastSynchronizedTimestamp;
        this.monochrome = monochrome;
        this.xmp = xmp;
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

    public int getAdditionalInfoSet() {
        return additionalInfoSet;
    }

    public void setAdditionalInfoSet(int additionalInfoSet) {
        this.additionalInfoSet = additionalInfoSet;
    }

    public int getEmbeddedXmp() {
        return embeddedXmp;
    }

    public void setEmbeddedXmp(int embeddedXmp) {
        this.embeddedXmp = embeddedXmp;
    }

    public int getExternalXmpIsDirty() {
        return externalXmpIsDirty;
    }

    public void setExternalXmpIsDirty(int externalXmpIsDirty) {
        this.externalXmpIsDirty = externalXmpIsDirty;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public int getIncrementalWhiteBalance() {
        return incrementalWhiteBalance;
    }

    public void setIncrementalWhiteBalance(int incrementalWhiteBalance) {
        this.incrementalWhiteBalance = incrementalWhiteBalance;
    }

    public String getInternalXmpDigest() {
        return internalXmpDigest;
    }

    public void setInternalXmpDigest(String internalXmpDigest) {
        this.internalXmpDigest = internalXmpDigest;
    }

    public int getIsRawFile() {
        return isRawFile;
    }

    public void setIsRawFile(int isRawFile) {
        this.isRawFile = isRawFile;
    }

    public String getLastSynchronizedHash() {
        return lastSynchronizedHash;
    }

    public void setLastSynchronizedHash(String lastSynchronizedHash) {
        this.lastSynchronizedHash = lastSynchronizedHash;
    }

    public String getLastSynchronizedTimestamp() {
        return lastSynchronizedTimestamp;
    }

    public void setLastSynchronizedTimestamp(String lastSynchronizedTimestamp) {
        this.lastSynchronizedTimestamp = lastSynchronizedTimestamp;
    }

    public String getMetadataPresetID() {
        return metadataPresetID;
    }

    public void setMetadataPresetID(String metadataPresetID) {
        this.metadataPresetID = metadataPresetID;
    }

    public String getMetadataVersion() {
        return metadataVersion;
    }

    public void setMetadataVersion(String metadataVersion) {
        this.metadataVersion = metadataVersion;
    }

    public int getMonochrome() {
        return monochrome;
    }

    public void setMonochrome(int monochrome) {
        this.monochrome = monochrome;
    }

    public String getXmp() {
        return xmp;
    }

    public void setXmp(String xmp) {
        this.xmp = xmp;
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
        if (!(object instanceof AdobeAdditionalMetadata)) {
            return false;
        }
        AdobeAdditionalMetadata other = (AdobeAdditionalMetadata) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobeAdditionalMetadata[ idLocal=" + idLocal + " ]";
    }
  }
