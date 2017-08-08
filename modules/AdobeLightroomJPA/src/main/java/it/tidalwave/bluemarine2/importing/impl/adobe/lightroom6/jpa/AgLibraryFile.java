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
@Table(name = "AgLibraryFile")
@NamedQueries({
    @NamedQuery(name = "AgLibraryFile.findAll", query = "SELECT a FROM AgLibraryFile a"),
    @NamedQuery(name = "AgLibraryFile.findByIdLocal", query = "SELECT a FROM AgLibraryFile a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryFile.findByIdGlobal", query = "SELECT a FROM AgLibraryFile a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AgLibraryFile.findByBaseName", query = "SELECT a FROM AgLibraryFile a WHERE a.baseName = :baseName"),
    @NamedQuery(name = "AgLibraryFile.findByErrorMessage", query = "SELECT a FROM AgLibraryFile a WHERE a.errorMessage = :errorMessage"),
    @NamedQuery(name = "AgLibraryFile.findByErrorTime", query = "SELECT a FROM AgLibraryFile a WHERE a.errorTime = :errorTime"),
    @NamedQuery(name = "AgLibraryFile.findByExtension", query = "SELECT a FROM AgLibraryFile a WHERE a.extension = :extension"),
    @NamedQuery(name = "AgLibraryFile.findByExternalModTime", query = "SELECT a FROM AgLibraryFile a WHERE a.externalModTime = :externalModTime"),
    @NamedQuery(name = "AgLibraryFile.findByFolder", query = "SELECT a FROM AgLibraryFile a WHERE a.folder = :folder"),
    @NamedQuery(name = "AgLibraryFile.findByIdxFilename", query = "SELECT a FROM AgLibraryFile a WHERE a.idxFilename = :idxFilename"),
    @NamedQuery(name = "AgLibraryFile.findByImportHash", query = "SELECT a FROM AgLibraryFile a WHERE a.importHash = :importHash"),
    @NamedQuery(name = "AgLibraryFile.findByLcIdxFilename", query = "SELECT a FROM AgLibraryFile a WHERE a.lcIdxFilename = :lcIdxFilename"),
    @NamedQuery(name = "AgLibraryFile.findByLcidxfilenameExtension", query = "SELECT a FROM AgLibraryFile a WHERE a.lcidxfilenameExtension = :lcidxfilenameExtension"),
    @NamedQuery(name = "AgLibraryFile.findByMd5", query = "SELECT a FROM AgLibraryFile a WHERE a.md5 = :md5"),
    @NamedQuery(name = "AgLibraryFile.findByModTime", query = "SELECT a FROM AgLibraryFile a WHERE a.modTime = :modTime"),
    @NamedQuery(name = "AgLibraryFile.findByOriginalFilename", query = "SELECT a FROM AgLibraryFile a WHERE a.originalFilename = :originalFilename"),
    @NamedQuery(name = "AgLibraryFile.findBySidecarExtensions", query = "SELECT a FROM AgLibraryFile a WHERE a.sidecarExtensions = :sidecarExtensions")})
public class AgLibraryFile implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Basic(optional = false)
    @Column(name = "baseName")
    private String baseName;
    @Column(name = "errorMessage")
    private String errorMessage;
    @Column(name = "errorTime")
    private String errorTime;
    @Basic(optional = false)
    @Column(name = "extension")
    private String extension;
    @Column(name = "externalModTime")
    private String externalModTime;
    @Basic(optional = false)
    @Column(name = "folder")
    private int folder;
    @Basic(optional = false)
    @Column(name = "idx_filename")
    private String idxFilename;
    @Column(name = "importHash")
    private String importHash;
    @Basic(optional = false)
    @Column(name = "lc_idx_filename")
    private String lcIdxFilename;
    @Basic(optional = false)
    @Column(name = "lc_idx_filenameExtension")
    private String lcidxfilenameExtension;
    @Column(name = "md5")
    private String md5;
    @Column(name = "modTime")
    private String modTime;
    @Basic(optional = false)
    @Column(name = "originalFilename")
    private String originalFilename;
    @Column(name = "sidecarExtensions")
    private String sidecarExtensions;

    public AgLibraryFile() {
    }

    public AgLibraryFile(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryFile(Integer idLocal, String idGlobal, String baseName, String extension, int folder, String idxFilename, String lcIdxFilename, String lcidxfilenameExtension, String originalFilename) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.baseName = baseName;
        this.extension = extension;
        this.folder = folder;
        this.idxFilename = idxFilename;
        this.lcIdxFilename = lcIdxFilename;
        this.lcidxfilenameExtension = lcidxfilenameExtension;
        this.originalFilename = originalFilename;
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

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(String errorTime) {
        this.errorTime = errorTime;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getExternalModTime() {
        return externalModTime;
    }

    public void setExternalModTime(String externalModTime) {
        this.externalModTime = externalModTime;
    }

    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public String getIdxFilename() {
        return idxFilename;
    }

    public void setIdxFilename(String idxFilename) {
        this.idxFilename = idxFilename;
    }

    public String getImportHash() {
        return importHash;
    }

    public void setImportHash(String importHash) {
        this.importHash = importHash;
    }

    public String getLcIdxFilename() {
        return lcIdxFilename;
    }

    public void setLcIdxFilename(String lcIdxFilename) {
        this.lcIdxFilename = lcIdxFilename;
    }

    public String getLcidxfilenameExtension() {
        return lcidxfilenameExtension;
    }

    public void setLcidxfilenameExtension(String lcidxfilenameExtension) {
        this.lcidxfilenameExtension = lcidxfilenameExtension;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getModTime() {
        return modTime;
    }

    public void setModTime(String modTime) {
        this.modTime = modTime;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getSidecarExtensions() {
        return sidecarExtensions;
    }

    public void setSidecarExtensions(String sidecarExtensions) {
        this.sidecarExtensions = sidecarExtensions;
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
        if (!(object instanceof AgLibraryFile)) {
            return false;
        }
        AgLibraryFile other = (AgLibraryFile) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryFile[ idLocal=" + idLocal + " ]";
    }
  }
