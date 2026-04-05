import { Component, OnInit, OnDestroy, ViewChild, ElementRef, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService, ChatMessage} from '../../services/chatbot.service';
import { AuthService} from '../../services/auth/auth.service';

@Component({
  selector: 'app-chatbot-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot-widget.component.html',
  styleUrls: ['./chatbot-widget.component.css']
})
export class ChatbotWidgetComponent implements OnInit, OnDestroy {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;

  isOpen = false;
  userInput = '';
  isLoading = false;
  messages: ChatMessage[] = [];
  isAuthenticated = false;
  private readonly STORAGE_KEY = 'astranimbusChatHistory';

  constructor(
    private chatbotService: ChatbotService,
    private authService: AuthService,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    // Verificar autenticación
    this.authService.currentUser$.subscribe(user => {
      this.isAuthenticated = !!user;
      if (!this.isAuthenticated) {
        this.addSystemMessage('Por favor inicia sesión para usar el chat de asistencia.');
      } else {
        this.loadChatHistory();
        if (this.messages.length === 0) {
          this.addWelcomeMessage();
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.saveChatHistory();
  }

  toggleChat(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      setTimeout(() => this.scrollToBottom(), 100);
    }
  }

  sendMessage(): void {
    if (!this.userInput.trim() || this.isLoading || !this.isAuthenticated) return;

    const userMessage: ChatMessage = {
      text: this.userInput,
      isUser: true,
      timestamp: new Date()
    };
    this.messages.push(userMessage);
    this.userInput = '';
    this.isLoading = true;
    this.scrollToBottom();
    this.saveChatHistory();

    this.chatbotService.sendMessage(userMessage.text).subscribe({
      next: (response) => {
        const botMessage: ChatMessage = {
          text: response.reply,
          isUser: false,
          timestamp: new Date()
        };
        this.messages.push(botMessage);
        this.isLoading = false;
        this.scrollToBottom();
        this.saveChatHistory();
      },
      error: (error) => {
        console.error('Chat error:', error);
        const errorMessage: ChatMessage = {
          text: error.message || 'Lo siento, hubo un error. Por favor intenta de nuevo.',
          isUser: false,
          timestamp: new Date()
        };
        this.messages.push(errorMessage);
        this.isLoading = false;
        this.scrollToBottom();
      }
    });
  }

  clearHistory(): void {
    this.messages = [];
    this.addWelcomeMessage();
    this.saveChatHistory();
  }

  private addWelcomeMessage(): void {
    this.messages.push({
      text: '¡Hola! Soy AstraBot, tu asistente virtual de AstraNimbus Aviation. ¿En qué puedo ayudarte hoy?',
      isUser: false,
      timestamp: new Date()
    });
  }

  private addSystemMessage(text: string): void {
    this.messages.push({
      text,
      isUser: false,
      timestamp: new Date()
    });
  }

  private loadChatHistory(): void {
    const saved = localStorage.getItem(this.STORAGE_KEY);
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        this.messages = parsed.map((msg: any) => ({
          ...msg,
          timestamp: new Date(msg.timestamp)
        }));
      } catch (e) {
        console.error('Error loading chat history:', e);
      }
    }
  }

  private saveChatHistory(): void {
    // Solo guardar las últimas 50 conversaciones
    const toSave = this.messages.slice(-50);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(toSave));
  }

  private scrollToBottom(): void {
    if (this.messagesContainer) {
      this.messagesContainer.nativeElement.scrollTop =
        this.messagesContainer.nativeElement.scrollHeight;
    }
  }
}
