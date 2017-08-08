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
@Table(name = "AgFolderContent")
@NamedQueries({
    @NamedQuery(name = "AgFolderContent.findAll", query = "SELECT a FROM AgFolderContent a"),
    @NamedQuery(name = "AgFolderContent.findByIdLocal", query = "SELECT a FROM AgFolderContent a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgFolderContent.findByIdGlobal", query = "SELECT a FROM AgFolderContent a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AgFolderContent.findByContainingFolder", query = "SELECT a FROM AgFolderContent a WHERE a.containingFolder = :containingFolder"),
    @NamedQuery(name = "AgFolderContent.findByContent", query = "SELECT a FROM AgFolderContent a WHERE a.content = :content"),
    @NamedQuery(name = "AgFolderContent.findByName", query = "SELECT a FROM AgFolderContent a WHERE a.name = :name"),
    @NamedQuery(name = "AgFolderContent.findByOwningModule", query = "SELECT a FROM AgFolderContent a WHERE a.owningModule = :owningModule")})
public class AgFolderContent implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Basic(optional = false)
    @Column(name = "containingFolder")
    private int containingFolder;
    @Column(name = "content")
    private String content;
    @Column(name = "name")
    private String name;
    @Column(name = "owningModule")
    private String owningModule;

    public AgFolderContent() {
    }

    public AgFolderContent(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgFolderContent(Integer idLocal, String idGlobal, int containingFolder) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.containingFolder = containingFolder;
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

    public int getContainingFolder() {
        return containingFolder;
    }

    public void setContainingFolder(int containingFolder) {
        this.containingFolder = containingFolder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof AgFolderContent)) {
            return false;
        }
        AgFolderContent other = (AgFolderContent) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgFolderContent[ idLocal=" + idLocal + " ]";
    }
  }
