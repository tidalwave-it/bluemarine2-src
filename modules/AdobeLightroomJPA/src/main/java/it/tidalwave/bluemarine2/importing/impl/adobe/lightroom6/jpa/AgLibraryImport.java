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
@Table(name = "AgLibraryImport")
@NamedQueries({
    @NamedQuery(name = "AgLibraryImport.findAll", query = "SELECT a FROM AgLibraryImport a"),
    @NamedQuery(name = "AgLibraryImport.findByIdLocal", query = "SELECT a FROM AgLibraryImport a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryImport.findByImageCount", query = "SELECT a FROM AgLibraryImport a WHERE a.imageCount = :imageCount"),
    @NamedQuery(name = "AgLibraryImport.findByImportDate", query = "SELECT a FROM AgLibraryImport a WHERE a.importDate = :importDate"),
    @NamedQuery(name = "AgLibraryImport.findByName", query = "SELECT a FROM AgLibraryImport a WHERE a.name = :name")})
public class AgLibraryImport implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "imageCount")
    private String imageCount;
    @Basic(optional = false)
    @Column(name = "importDate")
    private String importDate;
    @Column(name = "name")
    private String name;

    public AgLibraryImport() {
    }

    public AgLibraryImport(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryImport(Integer idLocal, String importDate) {
        this.idLocal = idLocal;
        this.importDate = importDate;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getImageCount() {
        return imageCount;
    }

    public void setImageCount(String imageCount) {
        this.imageCount = imageCount;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof AgLibraryImport)) {
            return false;
        }
        AgLibraryImport other = (AgLibraryImport) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryImport[ idLocal=" + idLocal + " ]";
    }
  }
