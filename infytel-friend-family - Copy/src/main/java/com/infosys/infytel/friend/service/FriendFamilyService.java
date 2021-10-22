package com.infosys.infytel.friend.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.infosys.infytel.friend.dto.FriendFamilyDTO;
import com.infosys.infytel.friend.entity.FriendFamily;
import com.infosys.infytel.friend.repository.FriendFamilyRepository;

@Service
public class FriendFamilyService {
	private static final Log LOGGER = LogFactory.getLog(FriendFamilyService.class);
	
	@Autowired
	FriendFamilyRepository friendRepo;

	// Create Friend Family
	public void saveFriend(@PathVariable Long phoneNo, @RequestBody FriendFamilyDTO friendDTO) {
		LOGGER.info("Creation request for customer "+phoneNo+" with data "+ friendDTO);
		friendDTO.setPhoneNo(phoneNo);
		FriendFamily friend = friendDTO.createFriend();
		friendRepo.save(friend);
	}
	
	public List<Long> getSpecificFriends(Long phoneNo){
		List<Long> friendList = new ArrayList<>();
		List<FriendFamily> friends = friendRepo.findByPhoneNo(phoneNo);
		for(FriendFamily friend : friends) {
			friendList.add(friend.getFriendAndFamily());
		}
		
		return friendList;
	}

}
