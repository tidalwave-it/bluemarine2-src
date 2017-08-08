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
@Table(name = "AgLibraryKeyword")
@NamedQueries({
    @NamedQuery(name = "AgLibraryKeyword.findAll", query = "SELECT a FROM AgLibraryKeyword a"),
    @NamedQuery(name = "AgLibraryKeyword.findByIdLocal", query = "SELECT a FROM AgLibraryKeyword a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryKeyword.findByIdGlobal", query = "SELECT a FROM AgLibraryKeyword a WHERE a.idGlobal = :idGlobal"),
    @NamedQuery(name = "AgLibraryKeyword.findByDateCreated", query = "SELECT a FROM AgLibraryKeyword a WHERE a.dateCreated = :dateCreated"),
    @NamedQuery(name = "AgLibraryKeyword.findByGenealogy", query = "SELECT a FROM AgLibraryKeyword a WHERE a.genealogy = :genealogy"),
    @NamedQuery(name = "AgLibraryKeyword.findByImageCountCache", query = "SELECT a FROM AgLibraryKeyword a WHERE a.imageCountCache = :imageCountCache"),
    @NamedQuery(name = "AgLibraryKeyword.findByIncludeOnExport", query = "SELECT a FROM AgLibraryKeyword a WHERE a.includeOnExport = :includeOnExport"),
    @NamedQuery(name = "AgLibraryKeyword.findByIncludeParents", query = "SELECT a FROM AgLibraryKeyword a WHERE a.includeParents = :includeParents"),
    @NamedQuery(name = "AgLibraryKeyword.findByIncludeSynonyms", query = "SELECT a FROM AgLibraryKeyword a WHERE a.includeSynonyms = :includeSynonyms"),
    @NamedQuery(name = "AgLibraryKeyword.findByKeywordType", query = "SELECT a FROM AgLibraryKeyword a WHERE a.keywordType = :keywordType"),
    @NamedQuery(name = "AgLibraryKeyword.findByLastApplied", query = "SELECT a FROM AgLibraryKeyword a WHERE a.lastApplied = :lastApplied"),
    @NamedQuery(name = "AgLibraryKeyword.findByLcName", query = "SELECT a FROM AgLibraryKeyword a WHERE a.lcName = :lcName"),
    @NamedQuery(name = "AgLibraryKeyword.findByName", query = "SELECT a FROM AgLibraryKeyword a WHERE a.name = :name"),
    @NamedQuery(name = "AgLibraryKeyword.findByParent", query = "SELECT a FROM AgLibraryKeyword a WHERE a.parent = :parent")})
public class AgLibraryKeyword implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "id_global")
    private String idGlobal;
    @Basic(optional = false)
    @Column(name = "dateCreated")
    private String dateCreated;
    @Basic(optional = false)
    @Column(name = "genealogy")
    private String genealogy;
    @Column(name = "imageCountCache")
    private String imageCountCache;
    @Basic(optional = false)
    @Column(name = "includeOnExport")
    private int includeOnExport;
    @Basic(optional = false)
    @Column(name = "includeParents")
    private int includeParents;
    @Basic(optional = false)
    @Column(name = "includeSynonyms")
    private int includeSynonyms;
    @Column(name = "keywordType")
    private String keywordType;
    @Column(name = "lastApplied")
    private String lastApplied;
    @Column(name = "lc_name")
    private String lcName;
    @Column(name = "name")
    private String name;
    @Column(name = "parent")
    private Integer parent;

    public AgLibraryKeyword() {
    }

    public AgLibraryKeyword(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryKeyword(Integer idLocal, String idGlobal, String dateCreated, String genealogy, int includeOnExport, int includeParents, int includeSynonyms) {
        this.idLocal = idLocal;
        this.idGlobal = idGlobal;
        this.dateCreated = dateCreated;
        this.genealogy = genealogy;
        this.includeOnExport = includeOnExport;
        this.includeParents = includeParents;
        this.includeSynonyms = includeSynonyms;
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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getGenealogy() {
        return genealogy;
    }

    public void setGenealogy(String genealogy) {
        this.genealogy = genealogy;
    }

    public String getImageCountCache() {
        return imageCountCache;
    }

    public void setImageCountCache(String imageCountCache) {
        this.imageCountCache = imageCountCache;
    }

    public int getIncludeOnExport() {
        return includeOnExport;
    }

    public void setIncludeOnExport(int includeOnExport) {
        this.includeOnExport = includeOnExport;
    }

    public int getIncludeParents() {
        return includeParents;
    }

    public void setIncludeParents(int includeParents) {
        this.includeParents = includeParents;
    }

    public int getIncludeSynonyms() {
        return includeSynonyms;
    }

    public void setIncludeSynonyms(int includeSynonyms) {
        this.includeSynonyms = includeSynonyms;
    }

    public String getKeywordType() {
        return keywordType;
    }

    public void setKeywordType(String keywordType) {
        this.keywordType = keywordType;
    }

    public String getLastApplied() {
        return lastApplied;
    }

    public void setLastApplied(String lastApplied) {
        this.lastApplied = lastApplied;
    }

    public String getLcName() {
        return lcName;
    }

    public void setLcName(String lcName) {
        this.lcName = lcName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
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
        if (!(object instanceof AgLibraryKeyword)) {
            return false;
        }
        AgLibraryKeyword other = (AgLibraryKeyword) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryKeyword[ idLocal=" + idLocal + " ]";
    }
  }
