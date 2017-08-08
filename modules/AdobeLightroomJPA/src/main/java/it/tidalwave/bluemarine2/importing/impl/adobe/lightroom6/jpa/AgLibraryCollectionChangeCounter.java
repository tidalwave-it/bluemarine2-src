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
@Table(name = "AgLibraryCollectionChangeCounter")
@NamedQueries({
    @NamedQuery(name = "AgLibraryCollectionChangeCounter.findAll", query = "SELECT a FROM AgLibraryCollectionChangeCounter a"),
    @NamedQuery(name = "AgLibraryCollectionChangeCounter.findByCollection", query = "SELECT a FROM AgLibraryCollectionChangeCounter a WHERE a.collection = :collection"),
    @NamedQuery(name = "AgLibraryCollectionChangeCounter.findByChangeCounter", query = "SELECT a FROM AgLibraryCollectionChangeCounter a WHERE a.changeCounter = :changeCounter")})
public class AgLibraryCollectionChangeCounter implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "collection")
    private String collection;
    @Column(name = "changeCounter")
    private String changeCounter;

    public AgLibraryCollectionChangeCounter() {
    }

    public AgLibraryCollectionChangeCounter(String collection) {
        this.collection = collection;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
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
        hash += (collection != null ? collection.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AgLibraryCollectionChangeCounter)) {
            return false;
        }
        AgLibraryCollectionChangeCounter other = (AgLibraryCollectionChangeCounter) object;
        if ((this.collection == null && other.collection != null) || (this.collection != null && !this.collection.equals(other.collection))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryCollectionChangeCounter[ collection=" + collection + " ]";
    }
  }
