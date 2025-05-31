<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>

<div id="createRoomModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>새 방 만들기</h2>
        <form action="createRoom" method="post">
            <div class="form-group">
                <input type="text" id="roomName" name="roomName" placeholder="방 이름" required>
            </div>
            <button type="submit" class="button">방 만들기</button>
        </form>
    </div>
</div>

<div id="deleteRoomModal" class="modal">
    <div class="modal-content">
        <p class="modal-text"></p>
        <div class="modal-buttons">
            <button id="confirmBtn">삭제</button>
            <button id="cancelBtn">취소</button>
        </div>
    </div>
</div>