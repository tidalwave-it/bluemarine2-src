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
@Table(name = "AgVideoInfo")
@NamedQueries({
    @NamedQuery(name = "AgVideoInfo.findAll", query = "SELECT a FROM AgVideoInfo a"),
    @NamedQuery(name = "AgVideoInfo.findByIdLocal", query = "SELECT a FROM AgVideoInfo a WHERE a.idLocal = :idLocal"),
    @NamedQuery(name = "AgVideoInfo.findByDuration", query = "SELECT a FROM AgVideoInfo a WHERE a.duration = :duration"),
    @NamedQuery(name = "AgVideoInfo.findByFrameRate", query = "SELECT a FROM AgVideoInfo a WHERE a.frameRate = :frameRate"),
    @NamedQuery(name = "AgVideoInfo.findByHasAudio", query = "SELECT a FROM AgVideoInfo a WHERE a.hasAudio = :hasAudio"),
    @NamedQuery(name = "AgVideoInfo.findByHasVideo", query = "SELECT a FROM AgVideoInfo a WHERE a.hasVideo = :hasVideo"),
    @NamedQuery(name = "AgVideoInfo.findByImage", query = "SELECT a FROM AgVideoInfo a WHERE a.image = :image"),
    @NamedQuery(name = "AgVideoInfo.findByPosterFrame", query = "SELECT a FROM AgVideoInfo a WHERE a.posterFrame = :posterFrame"),
    @NamedQuery(name = "AgVideoInfo.findByPosterFrameSetByUser", query = "SELECT a FROM AgVideoInfo a WHERE a.posterFrameSetByUser = :posterFrameSetByUser"),
    @NamedQuery(name = "AgVideoInfo.findByTrimEnd", query = "SELECT a FROM AgVideoInfo a WHERE a.trimEnd = :trimEnd"),
    @NamedQuery(name = "AgVideoInfo.findByTrimStart", query = "SELECT a FROM AgVideoInfo a WHERE a.trimStart = :trimStart")})
public class AgVideoInfo implements Serializable
  {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_local")
    private Integer idLocal;
    @Column(name = "duration")
    private String duration;
    @Column(name = "frame_rate")
    private String frameRate;
    @Basic(optional = false)
    @Column(name = "has_audio")
    private int hasAudio;
    @Basic(optional = false)
    @Column(name = "has_video")
    private int hasVideo;
    @Basic(optional = false)
    @Column(name = "image")
    private int image;
    @Basic(optional = false)
    @Column(name = "poster_frame")
    private String posterFrame;
    @Basic(optional = false)
    @Column(name = "poster_frame_set_by_user")
    private int posterFrameSetByUser;
    @Basic(optional = false)
    @Column(name = "trim_end")
    private String trimEnd;
    @Basic(optional = false)
    @Column(name = "trim_start")
    private String trimStart;

    public AgVideoInfo() {
    }

    public AgVideoInfo(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public AgVideoInfo(Integer idLocal, int hasAudio, int hasVideo, int image, String posterFrame, int posterFrameSetByUser, String trimEnd, String trimStart) {
        this.idLocal = idLocal;
        this.hasAudio = hasAudio;
        this.hasVideo = hasVideo;
        this.image = image;
        this.posterFrame = posterFrame;
        this.posterFrameSetByUser = posterFrameSetByUser;
        this.trimEnd = trimEnd;
        this.trimStart = trimStart;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(String frameRate) {
        this.frameRate = frameRate;
    }

    public int getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(int hasAudio) {
        this.hasAudio = hasAudio;
    }

    public int getHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(int hasVideo) {
        this.hasVideo = hasVideo;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getPosterFrame() {
        return posterFrame;
    }

    public void setPosterFrame(String posterFrame) {
        this.posterFrame = posterFrame;
    }

    public int getPosterFrameSetByUser() {
        return posterFrameSetByUser;
    }

    public void setPosterFrameSetByUser(int posterFrameSetByUser) {
        this.posterFrameSetByUser = posterFrameSetByUser;
    }

    public String getTrimEnd() {
        return trimEnd;
    }

    public void setTrimEnd(String trimEnd) {
        this.trimEnd = trimEnd;
    }

    public String getTrimStart() {
        return trimStart;
    }

    public void setTrimStart(String trimStart) {
        this.trimStart = trimStart;
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
        if (!(object instanceof AgVideoInfo)) {
            return false;
        }
        AgVideoInfo other = (AgVideoInfo) object;
        if ((this.idLocal == null && other.idLocal != null) || (this.idLocal != null && !this.idLocal.equals(other.idLocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.tidalwave.bluemarine2.model.impl.adobe.lightroom.AgVideoInfo[ idLocal=" + idLocal + " ]";
    }
  }
