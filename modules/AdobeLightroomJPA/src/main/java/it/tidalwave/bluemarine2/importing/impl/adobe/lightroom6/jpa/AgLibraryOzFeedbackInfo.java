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
@Table(name = "AgLibraryOzFeedbackInfo")
@NamedQueries({
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findAll", query = "SELECT a FROM AgLibraryOzFeedbackInfo a"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByIdLocal", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByImage", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.image = :image"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByLastFeedbackTime", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.lastFeedbackTime = :lastFeedbackTime"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByLastReadTime", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.lastReadTime = :lastReadTime"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByNewCommentCount", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.newCommentCount = :newCommentCount"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByNewFavoriteCount", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.newFavoriteCount = :newFavoriteCount"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByOzAssetId", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.ozAssetId = :ozAssetId"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByOzCatalogId", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.ozCatalogId = :ozCatalogId"),
    @NamedQuery(name = "AgLibraryOzFeedbackInfo.findByOzSpaceId", query = "SELECT a FROM AgLibraryOzFeedbackInfo a WHERE a.ozSpaceId = :ozSpaceId")})
public class AgLibraryOzFeedbackInfo implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "image")
    private String image;
    @Column(name = "lastFeedbackTime")
    private String lastFeedbackTime;
    @Column(name = "lastReadTime")
    private String lastReadTime;
    @Basic(optional = false)
    @Column(name = "newCommentCount")
    private String newCommentCount;
    @Basic(optional = false)
    @Column(name = "newFavoriteCount")
    private String newFavoriteCount;
    @Basic(optional = false)
    @Column(name = "ozAssetId")
    private String ozAssetId;
    @Basic(optional = false)
    @Column(name = "ozCatalogId")
    private String ozCatalogId;
    @Basic(optional = false)
    @Column(name = "ozSpaceId")
    private String ozSpaceId;

    public AgLibraryOzFeedbackInfo() {
    }

    public AgLibraryOzFeedbackInfo(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryOzFeedbackInfo(Integer idLocal, String image, String newCommentCount, String newFavoriteCount, String ozAssetId, String ozCatalogId, String ozSpaceId) {
        this.idLocal = idLocal;
        this.image = image;
        this.newCommentCount = newCommentCount;
        this.newFavoriteCount = newFavoriteCount;
        this.ozAssetId = ozAssetId;
        this.ozCatalogId = ozCatalogId;
        this.ozSpaceId = ozSpaceId;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastFeedbackTime() {
        return lastFeedbackTime;
    }

    public void setLastFeedbackTime(String lastFeedbackTime) {
        this.lastFeedbackTime = lastFeedbackTime;
    }

    public String getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(String lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public String getNewCommentCount() {
        return newCommentCount;
    }

    public void setNewCommentCount(String newCommentCount) {
        this.newCommentCount = newCommentCount;
    }

    public String getNewFavoriteCount() {
        return newFavoriteCount;
    }

    public void setNewFavoriteCount(String newFavoriteCount) {
        this.newFavoriteCount = newFavoriteCount;
    }

    public String getOzAssetId() {
        return ozAssetId;
    }

    public void setOzAssetId(String ozAssetId) {
        this.ozAssetId = ozAssetId;
    }

    public String getOzCatalogId() {
        return ozCatalogId;
    }

    public void setOzCatalogId(String ozCatalogId) {
        this.ozCatalogId = ozCatalogId;
    }

    public String getOzSpaceId() {
        return ozSpaceId;
    }

    public void setOzSpaceId(String ozSpaceId) {
        this.ozSpaceId = ozSpaceId;
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
        if (!(object instanceof AgLibraryOzFeedbackInfo)) {
            return false;
        }
        AgLibraryOzFeedbackInfo other = (AgLibraryOzFeedbackInfo) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryOzFeedbackInfo[ idLocal=" + idLocal + " ]";
    }
  }
