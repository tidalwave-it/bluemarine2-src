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
@Table(name = "AgLibraryPublishedCollectionImage")
@NamedQueries({
    @NamedQuery(name = "AgLibraryPublishedCollectionImage.findAll", query = "SELECT a FROM AgLibraryPublishedCollectionImage a"),
    @NamedQuery(name = "AgLibraryPublishedCollectionImage.findByIdLocal", query = "SELECT a FROM AgLibraryPublishedCollectionImage a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryPublishedCollectionImage.findByCollection", query = "SELECT a FROM AgLibraryPublishedCollectionImage a WHERE a.collection = :collection"),
    @NamedQuery(name = "AgLibraryPublishedCollectionImage.findByImage", query = "SELECT a FROM AgLibraryPublishedCollectionImage a WHERE a.image = :image"),
    @NamedQuery(name = "AgLibraryPublishedCollectionImage.findByPick", query = "SELECT a FROM AgLibraryPublishedCollectionImage a WHERE a.pick = :pick"),
    @NamedQuery(name = "AgLibraryPublishedCollectionImage.findByPositionInCollection", query = "SELECT a FROM AgLibraryPublishedCollectionImage a WHERE a.positionInCollection = :positionInCollection")})
public class AgLibraryPublishedCollectionImage implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "collection")
    private int collection;
    @Basic(optional = false)
    @Column(name = "image")
    private int image;
    @Basic(optional = false)
    @Column(name = "pick")
    private String pick;
    @Column(name = "positionInCollection")
    private String positionInCollection;

    public AgLibraryPublishedCollectionImage() {
    }

    public AgLibraryPublishedCollectionImage(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryPublishedCollectionImage(Integer idLocal, int collection, int image, String pick) {
        this.idLocal = idLocal;
        this.collection = collection;
        this.image = image;
        this.pick = pick;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getPick() {
        return pick;
    }

    public void setPick(String pick) {
        this.pick = pick;
    }

    public String getPositionInCollection() {
        return positionInCollection;
    }

    public void setPositionInCollection(String positionInCollection) {
        this.positionInCollection = positionInCollection;
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
        if (!(object instanceof AgLibraryPublishedCollectionImage)) {
            return false;
        }
        AgLibraryPublishedCollectionImage other = (AgLibraryPublishedCollectionImage) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryPublishedCollectionImage[ idLocal=" + idLocal + " ]";
    }
  }
