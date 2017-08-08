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
@Table(name = "AgLibraryCollectionImageChangeCounter")
@NamedQueries({
    @NamedQuery(name = "AgLibraryCollectionImageChangeCounter.findAll", query = "SELECT a FROM AgLibraryCollectionImageChangeCounter a"),
    @NamedQuery(name = "AgLibraryCollectionImageChangeCounter.findByCollectionImage", query = "SELECT a FROM AgLibraryCollectionImageChangeCounter a WHERE a.collectionImage = :collectionImage"),
    @NamedQuery(name = "AgLibraryCollectionImageChangeCounter.findByCollection", query = "SELECT a FROM AgLibraryCollectionImageChangeCounter a WHERE a.collection = :collection"),
    @NamedQuery(name = "AgLibraryCollectionImageChangeCounter.findByImage", query = "SELECT a FROM AgLibraryCollectionImageChangeCounter a WHERE a.image = :image"),
    @NamedQuery(name = "AgLibraryCollectionImageChangeCounter.findByChangeCounter", query = "SELECT a FROM AgLibraryCollectionImageChangeCounter a WHERE a.changeCounter = :changeCounter")})
public class AgLibraryCollectionImageChangeCounter implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "collectionImage")
    private String collectionImage;
    @Basic(optional = false)
    @Column(name = "collection")
    private String collection;
    @Basic(optional = false)
    @Column(name = "image")
    private String image;
    @Column(name = "changeCounter")
    private String changeCounter;

    public AgLibraryCollectionImageChangeCounter() {
    }

    public AgLibraryCollectionImageChangeCounter(String collectionImage) {
        this.collectionImage = collectionImage;
    }

    public AgLibraryCollectionImageChangeCounter(String collectionImage, String collection, String image) {
        this.collectionImage = collectionImage;
        this.collection = collection;
        this.image = image;
    }

    public String getCollectionImage() {
        return collectionImage;
    }

    public void setCollectionImage(String collectionImage) {
        this.collectionImage = collectionImage;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChangeCounter() {
        return changeCounter;
    }

    public void setChangeCounter(String changeCounter) {
        this.changeCounter = changeCounter;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (collectionImage != null ? collectionImage.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AgLibraryCollectionImageChangeCounter)) {
            return false;
        }
        AgLibraryCollectionImageChangeCounter other = (AgLibraryCollectionImageChangeCounter) object;
        if ((this.collectionImage == null && other.collectionImage != null) || (this.collectionImage != null && !this.collectionImage.equals(other.collectionImage))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryCollectionImageChangeCounter[ collectionImage=" + collectionImage + " ]";
    }
  }
