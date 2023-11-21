package com.thinkboxberlin.stepserv.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agents")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Agent {
    @Id
    String agentUuid;
    String agentName;
    Date lastSeen;
    String currentLocation;
}
