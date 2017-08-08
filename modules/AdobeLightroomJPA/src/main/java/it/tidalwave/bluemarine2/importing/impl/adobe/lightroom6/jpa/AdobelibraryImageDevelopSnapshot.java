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
@Table(name = "Adobe_libraryImageDevelopSnapshot")
@NamedQueries({
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findAll", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findByIdLocal", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findByIdGlobal", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findByDigest", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.digest = :digest"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findByHasDevelopAdjustments", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.hasDevelopAdjustments = :hasDevelopAdjustments"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findByImage", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.image = :image"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findByLocked", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.locked = :locked"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findByName", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.name = :name"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findBySnapshotID", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.snapshotID = :snapshotID"),
    @NamedQuery(name = "AdobelibraryImageDevelopSnapshot.findByText", query = "SELECT a FROM AdobelibraryImageDevelopSnapshot a WHERE a.text = :text")})
public class AdobelibraryImageDevelopSnapshot implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Column(name = "digest")
    private String digest;
    @Column(name = "hasDevelopAdjustments")
    private String hasDevelopAdjustments;
    @Column(name = "image")
    private Integer image;
    @Column(name = "locked")
    private String locked;
    @Column(name = "name")
    private String name;
    @Column(name = "snapshotID")
    private String snapshotID;
    @Column(name = "text")
    private String text;

    public AdobelibraryImageDevelopSnapshot() {
    }

    public AdobelibraryImageDevelopSnapshot(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AdobelibraryImageDevelopSnapshot(Integer idLocal, String idGlobal) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
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

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getHasDevelopAdjustments() {
        return hasDevelopAdjustments;
    }

    public void setHasDevelopAdjustments(String hasDevelopAdjustments) {
        this.hasDevelopAdjustments = hasDevelopAdjustments;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLocal != null ? idLocal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AdobelibraryImageDevelopSnapshot)) {
            return false;
        }
        AdobelibraryImageDevelopSnapshot other = (AdobelibraryImageDevelopSnapshot) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AdobelibraryImageDevelopSnapshot[ idLocal=" + idLocal + " ]";
    }
  }
