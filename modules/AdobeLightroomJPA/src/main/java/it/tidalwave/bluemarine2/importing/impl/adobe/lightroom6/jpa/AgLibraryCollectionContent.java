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
@Table(name = "AgLibraryCollectionContent")
@NamedQueries({
    @NamedQuery(name = "AgLibraryCollectionContent.findAll", query = "SELECT a FROM AgLibraryCollectionContent a"),
    @NamedQuery(name = "AgLibraryCollectionContent.findByIdLocal", query = "SELECT a FROM AgLibraryCollectionContent a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryCollectionContent.findByCollection", query = "SELECT a FROM AgLibraryCollectionContent a WHERE a.collection = :collection"),
    @NamedQuery(name = "AgLibraryCollectionContent.findByContent", query = "SELECT a FROM AgLibraryCollectionContent a WHERE a.content = :content"),
    @NamedQuery(name = "AgLibraryCollectionContent.findByOwningModule", query = "SELECT a FROM AgLibraryCollectionContent a WHERE a.owningModule = :owningModule")})
public class AgLibraryCollectionContent implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "collection")
    private int collection;
    @Column(name = "content")
    private String content;
    @Column(name = "owningModule")
    private String owningModule;

    public AgLibraryCollectionContent() {
    }

    public AgLibraryCollectionContent(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryCollectionContent(Integer idLocal, int collection) {
        this.idLocal = idLocal;
        this.collection = collection;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOwningModule() {
        return owningModule;
    }

    public void setOwningModule(String owningModule) {
        this.owningModule = owningModule;
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
        if (!(object instanceof AgLibraryCollectionContent)) {
            return false;
        }
        AgLibraryCollectionContent other = (AgLibraryCollectionContent) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryCollectionContent[ idLocal=" + idLocal + " ]";
    }
  }
