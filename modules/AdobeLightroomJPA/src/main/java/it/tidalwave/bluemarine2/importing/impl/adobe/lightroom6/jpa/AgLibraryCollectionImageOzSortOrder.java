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
@Table(name = "AgLibraryCollectionImageOzSortOrder")
@NamedQueries({
    @NamedQuery(name = "AgLibraryCollectionImageOzSortOrder.findAll", query = "SELECT a FROM AgLibraryCollectionImageOzSortOrder a"),
    @NamedQuery(name = "AgLibraryCollectionImageOzSortOrder.findByCollectionImage", query = "SELECT a FROM AgLibraryCollectionImageOzSortOrder a WHERE a.collectionImage = :collectionImage"),
    @NamedQuery(name = "AgLibraryCollectionImageOzSortOrder.findByCollection", query = "SELECT a FROM AgLibraryCollectionImageOzSortOrder a WHERE a.collection = :collection"),
    @NamedQuery(name = "AgLibraryCollectionImageOzSortOrder.findByPositionInCollection", query = "SELECT a FROM AgLibraryCollectionImageOzSortOrder a WHERE a.positionInCollection = :positionInCollection"),
    @NamedQuery(name = "AgLibraryCollectionImageOzSortOrder.findByOzSortOrder", query = "SELECT a FROM AgLibraryCollectionImageOzSortOrder a WHERE a.ozSortOrder = :ozSortOrder")})
public class AgLibraryCollectionImageOzSortOrder implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "collectionImage")
    private String collectionImage;
    @Basic(optional = false)
    @Column(name = "collection")
    private String collection;
    @Basic(optional = false)
    @Column(name = "positionInCollection")
    private String positionInCollection;
    @Basic(optional = false)
    @Column(name = "ozSortOrder")
    private String ozSortOrder;

    public AgLibraryCollectionImageOzSortOrder() {
    }

    public AgLibraryCollectionImageOzSortOrder(String collectionImage) {
        this.collectionImage = collectionImage;
    }

    public AgLibraryCollectionImageOzSortOrder(String collectionImage, String collection, String positionInCollection, String ozSortOrder) {
        this.collectionImage = collectionImage;
        this.collection = collection;
        this.positionInCollection = positionInCollection;
        this.ozSortOrder = ozSortOrder;
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

    public String getPositionInCollection() {
        return positionInCollection;
    }

    public void setPositionInCollection(String positionInCollection) {
        this.positionInCollection = positionInCollection;
    }

    public String getOzSortOrder() {
        return ozSortOrder;
    }

    public void setOzSortOrder(String ozSortOrder) {
        this.ozSortOrder = ozSortOrder;
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
        if (!(object instanceof AgLibraryCollectionImageOzSortOrder)) {
            return false;
        }
        AgLibraryCollectionImageOzSortOrder other = (AgLibraryCollectionImageOzSortOrder) object;
        if ((this.collectionImage == null && other.collectionImage != null) || (this.collectionImage != null && !this.collectionImage.equals(other.collectionImage))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryCollectionImageOzSortOrder[ collectionImage=" + collectionImage + " ]";
    }
  }
