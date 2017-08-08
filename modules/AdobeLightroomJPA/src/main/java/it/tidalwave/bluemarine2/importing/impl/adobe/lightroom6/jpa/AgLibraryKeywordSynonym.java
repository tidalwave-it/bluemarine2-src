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
@Table(name = "AgLibraryKeywordSynonym")
@NamedQueries({
    @NamedQuery(name = "AgLibraryKeywordSynonym.findAll", query = "SELECT a FROM AgLibraryKeywordSynonym a"),
    @NamedQuery(name = "AgLibraryKeywordSynonym.findByIdLocal", query = "SELECT a FROM AgLibraryKeywordSynonym a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryKeywordSynonym.findByKeyword", query = "SELECT a FROM AgLibraryKeywordSynonym a WHERE a.keyword = :keyword"),
    @NamedQuery(name = "AgLibraryKeywordSynonym.findByLcName", query = "SELECT a FROM AgLibraryKeywordSynonym a WHERE a.lcName = :lcName"),
    @NamedQuery(name = "AgLibraryKeywordSynonym.findByName", query = "SELECT a FROM AgLibraryKeywordSynonym a WHERE a.name = :name")})
public class AgLibraryKeywordSynonym implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "keyword")
    private int keyword;
    @Column(name = "lc_name")
    private String lcName;
    @Column(name = "name")
    private String name;

    public AgLibraryKeywordSynonym() {
    }

    public AgLibraryKeywordSynonym(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryKeywordSynonym(Integer idLocal, int keyword) {
        this.idLocal = idLocal;
        this.keyword = keyword;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public int getKeyword() {
        return keyword;
    }

    public void setKeyword(int keyword) {
        this.keyword = keyword;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLocal != null ? idLocal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AgLibraryKeywordSynonym)) {
            return false;
        }
        AgLibraryKeywordSynonym other = (AgLibraryKeywordSynonym) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryKeywordSynonym[ idLocal=" + idLocal + " ]";
    }
  }
