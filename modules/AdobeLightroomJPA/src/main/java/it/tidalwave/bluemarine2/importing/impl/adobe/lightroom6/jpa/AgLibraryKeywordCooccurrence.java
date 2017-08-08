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
@Table(name = "AgLibraryKeywordCooccurrence")
@NamedQueries({
    @NamedQuery(name = "AgLibraryKeywordCooccurrence.findAll", query = "SELECT a FROM AgLibraryKeywordCooccurrence a"),
    @NamedQuery(name = "AgLibraryKeywordCooccurrence.findByIdLocal", query = "SELECT a FROM AgLibraryKeywordCooccurrence a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryKeywordCooccurrence.findByTag1", query = "SELECT a FROM AgLibraryKeywordCooccurrence a WHERE a.tag1 = :tag1"),
    @NamedQuery(name = "AgLibraryKeywordCooccurrence.findByTag2", query = "SELECT a FROM AgLibraryKeywordCooccurrence a WHERE a.tag2 = :tag2"),
    @NamedQuery(name = "AgLibraryKeywordCooccurrence.findByValue", query = "SELECT a FROM AgLibraryKeywordCooccurrence a WHERE a.value = :value")})
public class AgLibraryKeywordCooccurrence implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "tag1")
    private String tag1;
    @Basic(optional = false)
    @Column(name = "tag2")
    private String tag2;
    @Basic(optional = false)
    @Column(name = "value")
    private String value;

    public AgLibraryKeywordCooccurrence() {
    }

    public AgLibraryKeywordCooccurrence(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryKeywordCooccurrence(Integer idLocal, String tag1, String tag2, String value) {
        this.idLocal = idLocal;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.value = value;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(object instanceof AgLibraryKeywordCooccurrence)) {
            return false;
        }
        AgLibraryKeywordCooccurrence other = (AgLibraryKeywordCooccurrence) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryKeywordCooccurrence[ idLocal=" + idLocal + " ]";
    }
  }
