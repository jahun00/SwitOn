import { study, auth } from './index';
// 스터디 데이터 조회 API
function fetchStudies() {
	return study.get('');
}

// 스터디 생성 API
function createStudy(studyData) {
	const formdata = new FormData();
	formdata.append('img', studyData.img);
	formdata.append('name', studyData.name);
	formdata.append('description', studyData.description);
	formdata.append('lowercategory_id', studyData.lowercategory_id);
	formdata.append('isPrivate', studyData.isPrivate);
	formdata.append('isRecruit', studyData.isRecruit);
	formdata.append('start_time', studyData.start_time);
	formdata.append('end_time', studyData.end_time);
	formdata.append('start_term', studyData.start_term);
	formdata.append('end_term', studyData.end_term);
	formdata.append('week', studyData.week);
	formdata.append('users_limit', studyData.users_limit);

	return study.post('', formdata);
}

function deleteStudy(studyId) {
	return study.delete(`${studyId}`);
}

function updateStudy(studyId, studyData) {
	return study.put(`${studyId}`, studyData);
}

function fetchStudy(studyId) {
	return study.get(`${studyId}`);
}

function fetchAttendance(studyId) {
	return study.get(`${studyId}/attend`);
}
function fetchRooms(studyId) {
	return study.get(`${studyId}/room`);
}
function createRoom(studyId, roomCode) {
	return study.post(`${studyId}/room`, roomCode);
}
function deleteRoom(studyId, roomId) {
	return study.delete(`${studyId}/room/${roomId}`);
}

function searchStudy(query) {
	return study.get(`?keyword=${query}`);
}
function searchOnlyStudy(query) {
	return study.get(`search?keyword=${query}`);
}
function JoinStudy(studyId) {
	return study.post(`${studyId}/join`);
}
// 스터디 Schedule
function fetchStudySchedule(studyId) {
	return study.get(`${studyId}/schedule`);
}
function fetchScheduleParticipate(studyId, scheduleId) {
	return study.get(`${studyId}/schedule/${scheduleId}/participate`);
}
function createScheduleParticipate(studyId, scheduleId) {
	return study.post(`${studyId}/schedule/${scheduleId}/participate`);
}
function deleteScheduleParticipate(studyId, scheduleId) {
	return study.delete(`${studyId}/schedule/${scheduleId}/participate`);
}
function checkInSchedule(studyId, scheduleId) {
	return study.put(`${studyId}/schedule/${scheduleId}/checkin`);
}
function checkOutSchedule(studyId, scheduleId) {
	return study.put(`${studyId}/schedule/${scheduleId}/checkout`);
}
function bestMember(studyId) {
	return study.get(`${studyId}/best`);
}
function bestArticle(studyId) {
	return study.get(`${studyId}/bestArticles`);
}
function popularStudy() {
	return auth.get('popularstudy');
}
export {
	fetchStudies,
	createStudy,
	deleteStudy,
	updateStudy,
	fetchStudy,
	searchStudy,
	searchOnlyStudy,
	JoinStudy,
	fetchStudySchedule,
	fetchScheduleParticipate,
	createScheduleParticipate,
	deleteScheduleParticipate,
	checkInSchedule,
	checkOutSchedule,
	bestMember,
	popularStudy,
	bestArticle,
	fetchRooms,
	createRoom,
	deleteRoom,
	fetchAttendance,
};
