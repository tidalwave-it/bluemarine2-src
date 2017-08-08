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
@Table(name = "AgLibraryFaceData")
@NamedQueries({
    @NamedQuery(name = "AgLibraryFaceData.findAll", query = "SELECT a FROM AgLibraryFaceData a"),
    @NamedQuery(name = "AgLibraryFaceData.findByIdLocal", query = "SELECT a FROM AgLibraryFaceData a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryFaceData.findByData", query = "SELECT a FROM AgLibraryFaceData a WHERE a.data = :data"),
    @NamedQuery(name = "AgLibraryFaceData.findByFace", query = "SELECT a FROM AgLibraryFaceData a WHERE a.face = :face")})
public class AgLibraryFaceData implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "data")
    private String data;
    @Basic(optional = false)
    @Column(name = "face")
    private int face;

    public AgLibraryFaceData() {
    }

    public AgLibraryFaceData(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryFaceData(Integer idLocal, int face) {
        this.idLocal = idLocal;
        this.face = face;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
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
        if (!(object instanceof AgLibraryFaceData)) {
            return false;
        }
        AgLibraryFaceData other = (AgLibraryFaceData) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryFaceData[ idLocal=" + idLocal + " ]";
    }
  }
