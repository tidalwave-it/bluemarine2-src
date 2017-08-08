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
@Table(name = "AgLibraryKeywordFace")
@NamedQueries({
    @NamedQuery(name = "AgLibraryKeywordFace.findAll", query = "SELECT a FROM AgLibraryKeywordFace a"),
    @NamedQuery(name = "AgLibraryKeywordFace.findByIdLocal", query = "SELECT a FROM AgLibraryKeywordFace a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgLibraryKeywordFace.findByFace", query = "SELECT a FROM AgLibraryKeywordFace a WHERE a.face = :face"),
    @NamedQuery(name = "AgLibraryKeywordFace.findByKeyFace", query = "SELECT a FROM AgLibraryKeywordFace a WHERE a.keyFace = :keyFace"),
    @NamedQuery(name = "AgLibraryKeywordFace.findByRankOrder", query = "SELECT a FROM AgLibraryKeywordFace a WHERE a.rankOrder = :rankOrder"),
    @NamedQuery(name = "AgLibraryKeywordFace.findByTag", query = "SELECT a FROM AgLibraryKeywordFace a WHERE a.tag = :tag"),
    @NamedQuery(name = "AgLibraryKeywordFace.findByUserPick", query = "SELECT a FROM AgLibraryKeywordFace a WHERE a.userPick = :userPick"),
    @NamedQuery(name = "AgLibraryKeywordFace.findByUserReject", query = "SELECT a FROM AgLibraryKeywordFace a WHERE a.userReject = :userReject")})
public class AgLibraryKeywordFace implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Basic(optional = false)
    @Column(name = "face")
    private int face;
    @Column(name = "keyFace")
    private Integer keyFace;
    @Column(name = "rankOrder")
    private String rankOrder;
    @Basic(optional = false)
    @Column(name = "tag")
    private int tag;
    @Column(name = "userPick")
    private Integer userPick;
    @Column(name = "userReject")
    private Integer userReject;

    public AgLibraryKeywordFace() {
    }

    public AgLibraryKeywordFace(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgLibraryKeywordFace(Integer idLocal, int face, int tag) {
        this.idLocal = idLocal;
        this.face = face;
        this.tag = tag;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public Integer getKeyFace() {
        return keyFace;
    }

    public void setKeyFace(Integer keyFace) {
        this.keyFace = keyFace;
    }

    public String getRankOrder() {
        return rankOrder;
    }

    public void setRankOrder(String rankOrder) {
        this.rankOrder = rankOrder;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Integer getUserPick() {
        return userPick;
    }

    public void setUserPick(Integer userPick) {
        this.userPick = userPick;
    }

    public Integer getUserReject() {
        return userReject;
    }

    public void setUserReject(Integer userReject) {
        this.userReject = userReject;
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
        if (!(object instanceof AgLibraryKeywordFace)) {
            return false;
        }
        AgLibraryKeywordFace other = (AgLibraryKeywordFace) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgLibraryKeywordFace[ idLocal=" + idLocal + " ]";
    }
  }
