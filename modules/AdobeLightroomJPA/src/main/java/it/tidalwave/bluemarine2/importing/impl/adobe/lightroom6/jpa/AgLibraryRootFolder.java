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
@Table(name = "AgLibraryRootFolder")
@NamedQueries({
    @NamedQuery(name = "AgLibraryRootFolder.findAll", query = "SELECT a FROM AgLibraryRootFolder a"),
    @NamedQuery(name = "AgLibraryRootFolder.findByIdLocal", query = "SELECT a FROM AgLibraryRootFolder a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryRootFolder.findByIdGlobal", query = "SELECT a FROM AgLibraryRootFolder a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AgLibraryRootFolder.findByAbsolutePath", query = "SELECT a FROM AgLibraryRootFolder a WHERE a.absolutePath = :absolutePath"),
    @NamedQuery(name = "AgLibraryRootFolder.findByName", query = "SELECT a FROM AgLibraryRootFolder a WHERE a.name = :name"),
    @NamedQuery(name = "AgLibraryRootFolder.findByRelativePathFromCatalog", query = "SELECT a FROM AgLibraryRootFolder a WHERE a.relativePathFromCatalog = :relativePathFromCatalog")})
public class AgLibraryRootFolder implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Basic(optional = false)
    @Column(name = "absolutePath")
    private String absolutePath;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "relativePathFromCatalog")
    private String relativePathFromCatalog;

    public AgLibraryRootFolder() {
    }

    public AgLibraryRootFolder(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryRootFolder(Integer idLocal, String idGlobal, String absolutePath, String name) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.absolutePath = absolutePath;
        this.name = name;
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

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelativePathFromCatalog() {
        return relativePathFromCatalog;
    }

    public void setRelativePathFromCatalog(String relativePathFromCatalog) {
        this.relativePathFromCatalog = relativePathFromCatalog;
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
        if (!(object instanceof AgLibraryRootFolder)) {
            return false;
        }
        AgLibraryRootFolder other = (AgLibraryRootFolder) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryRootFolder[ idLocal=" + idLocal + " ]";
    }
  }
