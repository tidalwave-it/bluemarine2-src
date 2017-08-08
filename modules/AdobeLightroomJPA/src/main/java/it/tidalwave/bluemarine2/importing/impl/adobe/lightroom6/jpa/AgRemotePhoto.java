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
@Table(name = "AgRemotePhoto")
@NamedQueries({
    @NamedQuery(name = "AgRemotePhoto.findAll", query = "SELECT a FROM AgRemotePhoto a"),
    @NamedQuery(name = "AgRemotePhoto.findByIdLocal", query = "SELECT a FROM AgRemotePhoto a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgRemotePhoto.findByIdGlobal", query = "SELECT a FROM AgRemotePhoto a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AgRemotePhoto.findByCollection", query = "SELECT a FROM AgRemotePhoto a WHERE a.collection = :collection"),
    @NamedQuery(name = "AgRemotePhoto.findByCommentCount", query = "SELECT a FROM AgRemotePhoto a WHERE a.commentCount = :commentCount"),
    @NamedQuery(name = "AgRemotePhoto.findByDevelopSettingsDigest", query = "SELECT a FROM AgRemotePhoto a WHERE a.developSettingsDigest = :developSettingsDigest"),
    @NamedQuery(name = "AgRemotePhoto.findByFileContentsHash", query = "SELECT a FROM AgRemotePhoto a WHERE a.fileContentsHash = :fileContentsHash"),
    @NamedQuery(name = "AgRemotePhoto.findByFileModTimestamp", query = "SELECT a FROM AgRemotePhoto a WHERE a.fileModTimestamp = :fileModTimestamp"),
    @NamedQuery(name = "AgRemotePhoto.findByMetadataDigest", query = "SELECT a FROM AgRemotePhoto a WHERE a.metadataDigest = :metadataDigest"),
    @NamedQuery(name = "AgRemotePhoto.findByMostRecentCommentTime", query = "SELECT a FROM AgRemotePhoto a WHERE a.mostRecentCommentTime = :mostRecentCommentTime"),
    @NamedQuery(name = "AgRemotePhoto.findByOrientation", query = "SELECT a FROM AgRemotePhoto a WHERE a.orientation = :orientation"),
    @NamedQuery(name = "AgRemotePhoto.findByPhoto", query = "SELECT a FROM AgRemotePhoto a WHERE a.photo = :photo"),
    @NamedQuery(name = "AgRemotePhoto.findByPhotoNeedsUpdating", query = "SELECT a FROM AgRemotePhoto a WHERE a.photoNeedsUpdating = :photoNeedsUpdating"),
    @NamedQuery(name = "AgRemotePhoto.findByPublishCount", query = "SELECT a FROM AgRemotePhoto a WHERE a.publishCount = :publishCount"),
    @NamedQuery(name = "AgRemotePhoto.findByRemoteId", query = "SELECT a FROM AgRemotePhoto a WHERE a.remoteId = :remoteId"),
    @NamedQuery(name = "AgRemotePhoto.findByServiceAggregateRating", query = "SELECT a FROM AgRemotePhoto a WHERE a.serviceAggregateRating = :serviceAggregateRating"),
    @NamedQuery(name = "AgRemotePhoto.findByUrl", query = "SELECT a FROM AgRemotePhoto a WHERE a.url = :url")})
public class AgRemotePhoto implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Basic(optional = false)
    @Column(name = "collection")
    private int collection;
    @Column(name = "commentCount")
    private String commentCount;
    @Column(name = "developSettingsDigest")
    private String developSettingsDigest;
    @Column(name = "fileContentsHash")
    private String fileContentsHash;
    @Column(name = "fileModTimestamp")
    private String fileModTimestamp;
    @Column(name = "metadataDigest")
    private String metadataDigest;
    @Column(name = "mostRecentCommentTime")
    private String mostRecentCommentTime;
    @Column(name = "orientation")
    private String orientation;
    @Basic(optional = false)
    @Column(name = "photo")
    private int photo;
    @Column(name = "photoNeedsUpdating")
    private String photoNeedsUpdating;
    @Column(name = "publishCount")
    private String publishCount;
    @Column(name = "remoteId")
    private String remoteId;
    @Column(name = "serviceAggregateRating")
    private String serviceAggregateRating;
    @Column(name = "url")
    private String url;

    public AgRemotePhoto() {
    }

    public AgRemotePhoto(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgRemotePhoto(Integer idLocal, String idGlobal, int collection, int photo) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.collection = collection;
        this.photo = photo;
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

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getDevelopSettingsDigest() {
        return developSettingsDigest;
    }

    public void setDevelopSettingsDigest(String developSettingsDigest) {
        this.developSettingsDigest = developSettingsDigest;
    }

    public String getFileContentsHash() {
        return fileContentsHash;
    }

    public void setFileContentsHash(String fileContentsHash) {
        this.fileContentsHash = fileContentsHash;
    }

    public String getFileModTimestamp() {
        return fileModTimestamp;
    }

    public void setFileModTimestamp(String fileModTimestamp) {
        this.fileModTimestamp = fileModTimestamp;
    }

    public String getMetadataDigest() {
        return metadataDigest;
    }

    public void setMetadataDigest(String metadataDigest) {
        this.metadataDigest = metadataDigest;
    }

    public String getMostRecentCommentTime() {
        return mostRecentCommentTime;
    }

    public void setMostRecentCommentTime(String mostRecentCommentTime) {
        this.mostRecentCommentTime = mostRecentCommentTime;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getPhotoNeedsUpdating() {
        return photoNeedsUpdating;
    }

    public void setPhotoNeedsUpdating(String photoNeedsUpdating) {
        this.photoNeedsUpdating = photoNeedsUpdating;
    }

    public String getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(String publishCount) {
        this.publishCount = publishCount;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getServiceAggregateRating() {
        return serviceAggregateRating;
    }

    public void setServiceAggregateRating(String serviceAggregateRating) {
        this.serviceAggregateRating = serviceAggregateRating;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        if (!(object instanceof AgRemotePhoto)) {
            return false;
        }
        AgRemotePhoto other = (AgRemotePhoto) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgRemotePhoto[ idLocal=" + idLocal + " ]";
    }
  }
