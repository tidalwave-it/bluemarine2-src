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
@Table(name = "AgSpecialSourceContent")
@NamedQueries({
    @NamedQuery(name = "AgSpecialSourceContent.findAll", query = "SELECT a FROM AgSpecialSourceContent a"),
    @NamedQuery(name = "AgSpecialSourceContent.findByIdLocal", query = "SELECT a FROM AgSpecialSourceContent a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgSpecialSourceContent.findByContent", query = "SELECT a FROM AgSpecialSourceContent a WHERE a.content = :content"),
    @NamedQuery(name = "AgSpecialSourceContent.findByOwningModule", query = "SELECT a FROM AgSpecialSourceContent a WHERE a.owningModule = :owningModule"),
    @NamedQuery(name = "AgSpecialSourceContent.findBySource", query = "SELECT a FROM AgSpecialSourceContent a WHERE a.source = :source")})
public class AgSpecialSourceContent implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "content")
    private String content;
    @Column(name = "owningModule")
    private String owningModule;
    @Basic(optional = false)
    @Column(name = "source")
    private String source;

    public AgSpecialSourceContent() {
    }

    public AgSpecialSourceContent(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgSpecialSourceContent(Integer idLocal, String source) {
        this.idLocal = idLocal;
        this.source = source;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
        if (!(object instanceof AgSpecialSourceContent)) {
            return false;
        }
        AgSpecialSourceContent other = (AgSpecialSourceContent) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgSpecialSourceContent[ idLocal=" + idLocal + " ]";
    }
  }
