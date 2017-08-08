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
@Table(name = "AgPhotoComment")
@NamedQueries({
    @NamedQuery(name = "AgPhotoComment.findAll", query = "SELECT a FROM AgPhotoComment a"),
    @NamedQuery(name = "AgPhotoComment.findByIdLocal", query = "SELECT a FROM AgPhotoComment a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgPhotoComment.findByIdGlobal", query = "SELECT a FROM AgPhotoComment a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AgPhotoComment.findByComment", query = "SELECT a FROM AgPhotoComment a WHERE a.comment = :comment"),
    @NamedQuery(name = "AgPhotoComment.findByCommentRealname", query = "SELECT a FROM AgPhotoComment a WHERE a.commentRealname = :commentRealname"),
    @NamedQuery(name = "AgPhotoComment.findByCommentUsername", query = "SELECT a FROM AgPhotoComment a WHERE a.commentUsername = :commentUsername"),
    @NamedQuery(name = "AgPhotoComment.findByDateCreated", query = "SELECT a FROM AgPhotoComment a WHERE a.dateCreated = :dateCreated"),
    @NamedQuery(name = "AgPhotoComment.findByPhoto", query = "SELECT a FROM AgPhotoComment a WHERE a.photo = :photo"),
    @NamedQuery(name = "AgPhotoComment.findByRemoteId", query = "SELECT a FROM AgPhotoComment a WHERE a.remoteId = :remoteId"),
    @NamedQuery(name = "AgPhotoComment.findByRemotePhoto", query = "SELECT a FROM AgPhotoComment a WHERE a.remotePhoto = :remotePhoto"),
    @NamedQuery(name = "AgPhotoComment.findByUrl", query = "SELECT a FROM AgPhotoComment a WHERE a.url = :url")})
public class AgPhotoComment implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Column(name = "comment")
    private String comment;
    @Column(name = "commentRealname")
    private String commentRealname;
    @Column(name = "commentUsername")
    private String commentUsername;
    @Column(name = "dateCreated")
    private String dateCreated;
    @Basic(optional = false)
    @Column(name = "photo")
    private int photo;
    @Basic(optional = false)
    @Column(name = "remoteId")
    private String remoteId;
    @Column(name = "remotePhoto")
    private Integer remotePhoto;
    @Column(name = "url")
    private String url;

    public AgPhotoComment() {
    }

    public AgPhotoComment(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgPhotoComment(Integer idLocal, String idGlobal, int photo, String remoteId) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.photo = photo;
        this.remoteId = remoteId;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentRealname() {
        return commentRealname;
    }

    public void setCommentRealname(String commentRealname) {
        this.commentRealname = commentRealname;
    }

    public String getCommentUsername() {
        return commentUsername;
    }

    public void setCommentUsername(String commentUsername) {
        this.commentUsername = commentUsername;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public Integer getRemotePhoto() {
        return remotePhoto;
    }

    public void setRemotePhoto(Integer remotePhoto) {
        this.remotePhoto = remotePhoto;
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
        if (!(object instanceof AgPhotoComment)) {
            return false;
        }
        AgPhotoComment other = (AgPhotoComment) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgPhotoComment[ idLocal=" + idLocal + " ]";
    }
  }
