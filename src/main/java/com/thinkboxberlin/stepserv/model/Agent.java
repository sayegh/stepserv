package com.thinkboxberlin.stepserv.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agents",
    indexes = { @Index(name = "lastseen_index", columnList = "lastSeen", unique = false) })
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Agent {
    @Id
    String agentUuid;
    String agentName;
    Date lastSeen;
    String currentLocation;
    @ElementCollection
    List<String> tags = new ArrayList<String>();
}
