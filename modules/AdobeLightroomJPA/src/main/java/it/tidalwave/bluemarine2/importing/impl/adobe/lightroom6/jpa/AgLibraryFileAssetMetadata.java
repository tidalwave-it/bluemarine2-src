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
@Table(name = "AgLibraryFileAssetMetadata")
@NamedQueries({
    @NamedQuery(name = "AgLibraryFileAssetMetadata.findAll", query = "SELECT a FROM AgLibraryFileAssetMetadata a"),
    @NamedQuery(name = "AgLibraryFileAssetMetadata.findByFileId", query = "SELECT a FROM AgLibraryFileAssetMetadata a WHERE a.fileId = :fileId"),
    @NamedQuery(name = "AgLibraryFileAssetMetadata.findBySha256", query = "SELECT a FROM AgLibraryFileAssetMetadata a WHERE a.sha256 = :sha256"),
    @NamedQuery(name = "AgLibraryFileAssetMetadata.findByFileSize", query = "SELECT a FROM AgLibraryFileAssetMetadata a WHERE a.fileSize = :fileSize")})
public class AgLibraryFileAssetMetadata implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "fileId")
    private String fileId;
    @Basic(optional = false)
    @Column(name = "sha256")
    private String sha256;
    @Column(name = "fileSize")
    private String fileSize;

    public AgLibraryFileAssetMetadata() {
    }

    public AgLibraryFileAssetMetadata(String fileId) {
        this.fileId = fileId;
    }

    public AgLibraryFileAssetMetadata(String fileId, String sha256) {
        this.fileId = fileId;
        this.sha256 = sha256;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fileId != null ? fileId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AgLibraryFileAssetMetadata)) {
            return false;
        }
        AgLibraryFileAssetMetadata other = (AgLibraryFileAssetMetadata) object;
        if ((this.fileId == null && other.fileId != null) || (this.fileId != null && !this.fileId.equals(other.fileId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryFileAssetMetadata[ fileId=" + fileId + " ]";
    }
  }
