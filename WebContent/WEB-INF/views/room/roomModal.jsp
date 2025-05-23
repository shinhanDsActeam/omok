<div id="createRoomModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>새 방 만들기</h2>
        <form action="createRoom" method="post">
            <div class="form-group">
                <label for="roomName">방 이름:</label>
                <input type="text" id="roomName" name="roomName" required>
            </div>
            <button type="submit" class="button">방 만들기</button>
        </form>
    </div>
</div>