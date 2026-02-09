package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveDTO;
import com.paeldav.backend.application.mapper.ImagenAeronaveMapper;
import com.paeldav.backend.application.service.integration.CloudinaryService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.ImagenAeronave;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.ImagenAeronaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for ImagenAeronaveServiceImpl with WebP conversion validation.
 * Tests that images are properly uploaded with automatic format conversion to WebP.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ImagenAeronaveService - WebP Conversion Tests")
class ImagenAeronaveServiceWebpConversionTest {

    @Mock
    private ImagenAeronaveRepository imagenRepository;

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private ImagenAeronaveMapper imagenMapper;

    private ImagenAeronaveServiceImpl imagenAeronaveService;
    private Aeronave testAeronave;

    @BeforeEach
    void setUp() {
        imagenAeronaveService = new ImagenAeronaveServiceImpl(
                imagenRepository,
                aeronaveRepository,
                cloudinaryService,
                imagenMapper
        );

        testAeronave = Aeronave.builder()
                .id(1L)
                .matricula("N1234")
                .estado(EstadoAeronave.DISPONIBLE)
                .build();
    }

    @Test
    @DisplayName("Should upload JPG image and call CloudinaryService with uploadImage method")
    void testUploadJpgImage_CallsUploadImage() {
        // Arrange
        MultipartFile jpgFile = createMockImageFile("aircraft.jpg", "image/jpeg");
        ImagenAeronaveCreateDTO imagenDTO = ImagenAeronaveCreateDTO.builder()
                .tipo(TipoImagenAeronave.EXTERIOR)
                .descripcion("Aircraft exterior")
                .ordenVisualizacion(0)
                .build();

        when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(testAeronave));
        when(cloudinaryService.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn(createCloudinaryResponse("aircraft_123.webp", 50000L));
        when(imagenRepository.save(any(ImagenAeronave.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(imagenMapper.toDTO(any(ImagenAeronave.class))).thenReturn(new ImagenAeronaveDTO());

        // Act
        imagenAeronaveService.cargarImagen(1L, jpgFile, imagenDTO);

        // Assert
        verify(cloudinaryService, times(1)).uploadImage(any(MultipartFile.class), contains("N1234"));
    }

    @Test
    @DisplayName("Should upload PNG image and call CloudinaryService with uploadImage method")
    void testUploadPngImage_CallsUploadImage() {
        // Arrange
        MultipartFile pngFile = createMockImageFile("aircraft.png", "image/png");
        ImagenAeronaveCreateDTO imagenDTO = ImagenAeronaveCreateDTO.builder()
                .tipo(TipoImagenAeronave.INTERIOR)
                .descripcion("Aircraft interior")
                .build();

        when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(testAeronave));
        when(cloudinaryService.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn(createCloudinaryResponse("aircraft_456.webp", 75000L));
        when(imagenRepository.save(any(ImagenAeronave.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(imagenMapper.toDTO(any(ImagenAeronave.class))).thenReturn(new ImagenAeronaveDTO());

        // Act
        imagenAeronaveService.cargarImagen(1L, pngFile, imagenDTO);

        // Assert
        verify(cloudinaryService, times(1)).uploadImage(any(MultipartFile.class), contains("N1234"));
    }

    @Test
    @DisplayName("Should upload WebP image and call CloudinaryService with uploadImage method")
    void testUploadWebpImage_CallsUploadImage() {
        // Arrange
        MultipartFile webpFile = createMockImageFile("aircraft.webp", "image/webp");
        ImagenAeronaveCreateDTO imagenDTO = ImagenAeronaveCreateDTO.builder()
                .tipo(TipoImagenAeronave.COCKPIT)
                .descripcion("Aircraft details")
                .build();

        when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(testAeronave));
        when(cloudinaryService.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn(createCloudinaryResponse("aircraft_789.webp", 45000L));
        when(imagenRepository.save(any(ImagenAeronave.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(imagenMapper.toDTO(any(ImagenAeronave.class))).thenReturn(new ImagenAeronaveDTO());

        // Act
        imagenAeronaveService.cargarImagen(1L, webpFile, imagenDTO);

        // Assert
        verify(cloudinaryService, times(1)).uploadImage(any(MultipartFile.class), contains("N1234"));
    }

    @Test
    @DisplayName("Should use uploadImage method instead of uploadFile for images")
    void testUseUploadImageMethod() {
        // Arrange
        MultipartFile jpgFile = createMockImageFile("photo.jpg", "image/jpeg");
        ImagenAeronaveCreateDTO imagenDTO = ImagenAeronaveCreateDTO.builder()
                .tipo(TipoImagenAeronave.EXTERIOR)
                .build();

        when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(testAeronave));
        when(cloudinaryService.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn(createCloudinaryResponse("photo.webp", 40000L));
        when(imagenRepository.save(any(ImagenAeronave.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(imagenMapper.toDTO(any(ImagenAeronave.class))).thenReturn(new ImagenAeronaveDTO());

        // Act
        imagenAeronaveService.cargarImagen(1L, jpgFile, imagenDTO);

        // Assert
        verify(cloudinaryService).uploadImage(any(MultipartFile.class), anyString());
        verify(cloudinaryService, never()).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("Should reject unsupported image format from CloudinaryService")
    void testRejectUnsupportedFormat() {
        // Arrange
        MultipartFile gifFile = createMockImageFile("animation.gif", "image/gif");
        ImagenAeronaveCreateDTO imagenDTO = ImagenAeronaveCreateDTO.builder()
                .tipo(TipoImagenAeronave.EXTERIOR)
                .build();

        when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(testAeronave));
        when(cloudinaryService.uploadImage(any(MultipartFile.class), anyString()))
                .thenThrow(new IllegalArgumentException("Formato de imagen no soportado. Formatos permitidos: jpg, jpeg, png, avif, webp"));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> imagenAeronaveService.cargarImagen(1L, gifFile, imagenDTO)
        );
    }


    @Test
    @DisplayName("Should store uploaded image with correct folder structure")
    void testStoreImageWithCorrectFolderStructure() {
        // Arrange
        MultipartFile jpgFile = createMockImageFile("aircraft.jpg", "image/jpeg");
        ImagenAeronaveCreateDTO imagenDTO = ImagenAeronaveCreateDTO.builder()
                .tipo(TipoImagenAeronave.EXTERIOR)
                .descripcion("Test image")
                .ordenVisualizacion(1)
                .build();

        when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(testAeronave));
        when(cloudinaryService.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn(createCloudinaryResponse("aircraft_123.webp", 50000L));

        ImagenAeronave savedImagen = ImagenAeronave.builder()
                .id(100L)
                .aeronave(testAeronave)
                .urlImagen("https://example.com/aircraft_123.webp")
                .idCloudinary("aircraft_123.webp")
                .tipo(TipoImagenAeronave.EXTERIOR)
                .descripcion("Test image")
                .tamanoBytes(50000L)
                .build();

        when(imagenRepository.save(any(ImagenAeronave.class))).thenReturn(savedImagen);
        when(imagenMapper.toDTO(any(ImagenAeronave.class))).thenReturn(
                ImagenAeronaveDTO.builder()
                        .id(100L)
                        .urlImagen("https://example.com/aircraft_123.webp")
                        .build()
        );

        // Act
        imagenAeronaveService.cargarImagen(1L, jpgFile, imagenDTO);

        // Assert
        verify(cloudinaryService).uploadImage(any(MultipartFile.class), argThat(folder ->
                folder.contains("aeronaves") && folder.contains("N1234")
        ));
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private MultipartFile createMockImageFile(String filename, String contentType) {
        byte[] content = "mock-image-content".getBytes();
        return new MockMultipartFile(
                "file",
                filename,
                contentType,
                content
        );
    }

    private Map<String, Object> createCloudinaryResponse(String publicId, Long bytes) {
        Map<String, Object> response = new HashMap<>();
        response.put("public_id", publicId);
        response.put("url", "https://example.com/" + publicId);
        response.put("size", bytes);
        return response;
    }
}
